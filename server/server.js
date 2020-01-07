//네트워크 기본 설정
const port=8081
const dbAddress = "mongodb+srv://admin:immersion@cluster0-7lhcw.mongodb.net/test?retryWrites=true&w=majority"
const cryptoIterationCount = 100
const jwtSecret = 'secret'
const md5Salt = 'secret'

//일반 모듈 로딩
const logger = require('./config/winston')
const md5 = require('md5')
const crypto = require('crypto')
const uuid = require('uuid')

//네트워크 관련 모듈 로딩
const http = require('http')
const socketio = require('socket.io')
const jwt = require('jsonwebtoken')
const server = http.createServer(function(req,res){}).listen(port,function() {
    logger.info('Server is now running at the port ' + port)
})
const io = socketio.listen(server)

//몽고DB 연결
const mongoose = require('mongoose')
mongoose.connect(dbAddress)
const db = mongoose.connection
db.on('error',function() {
    logger.info('Connection to DB Failed')
})
db.on('open',function() {
    logger.info('Connected to mongoDB')
})

//몽고DB 스키마 작성
const userSchema = mongoose.Schema({
    id:'String',
    password:'String',
    fbToken: 'String',
    jsonWebToken: 'String',
    name:'String',
    school:'String',
    email:'String',
    gender:'String',
    phone:'String',
    chatroomid:'String'
})
const algorithmDataSchema = mongoose.Schema({
    id:'String',
    member:[String],
    departureDateFrom:'String',
    departureDateTo:'String',
    departureLocation:'String',
    destinationLocation:'String',
})
const chatroomSchema = mongoose.Schema({
    id:'String',
    member:[String], //id of each memebers
    message:[{id:String, nickname:String, date:String, message:'String', index:Number}]
})

const userModel = mongoose.model('user',userSchema);
const algorithmDataModel = mongoose.model('algorithmData', algorithmDataSchema)
const chatroomModel = mongoose.model('chatroom', chatroomSchema)

//소켓에서 정보 가져오며 정보 처리
io.sockets.on('connection', function(socket) {
    logger.info('Socket ID: ' + socket.id + ' Connected')
    //클라이언트가 로그인에 접속할 경우
    socket.on('client_login', function(data) {
        logger.info('[client_login]'+data)
        let localId = data.id;
        let localPassword = md5(data.password + md5Salt);

        let findConditionLocalUser = {
            id: localId,
            password: localPassword
        }

        userModel.findOne(findConditionLocalUser).exec(function(err, user){
            if(err) {
                socket.emit('server_result_login',{type:'error', data:'error occured'})
                logger.info('[error]' + 'DB Not found')
            } else if(!user) {
                socket.emit('server_result_login',{type:'failed', data:'Incorrect id/password'})
                logger.info('[failed]Incorrect id/password')
            } else if(user) {
                socket.emit('server_result_login',{type:'success', data:user, token:user.jsonWebToken})
                logger.info('[success]' + user.jsonWebToken)
            }
        })
    })

    socket.on('client_check_session', function(data) {
        logger.info('[client_check_session]')

        sessionCallback(data, function(user) {
            socket.emit('server_result_check_session', {type:'success', data:user})
        })
    })

    socket.on('client_check_duplicate', function(data) {
        logger.info('[client_check_cuplicate]')
        
        let findConditionLocalUser = {
            id: data.id,
        }

        userModel.findOne(findConditionLocalUser).exec(function(err, user) {
            if(err) {
                socket.emit('server_result_check_duplicate',{type:'error', data:'error occured'})
                logger.info('[error]' + 'DB Not found')
            } else if(!user) {
                socket.emit('server_result_check_duplicate',{type:'success', data:'id not exists'})
                logger.info('[success]')
            } else {
                socket.emit('server_result_check_duplicate',{type:'failed', data:'id already exists'})
                logger.info('[failed]' + 'dupulicated id')
            }
        })
    })

    socket.on('client_login_facebook', function(data) {
        logger.info('[client_login_facebook]' + data);
        let fbUserId = data.id;
        let fbAccessToken = data.fbToken;

        let findConditionFbUserId = {
            id: fbUserId
        }
        userModel.findOne(findConditionLocalUser).exec(function(err, user) {
            if(err) {
                socket.emit('server_result_login_facebook',{type:'error', data:'error occured'})
            } else if(!user) {
                fbSignup(fbUserId, fbAccessToken, function(err, savedUser) {
                    if(err) {
                        socket.emit('server_result_login_facebook',{type:'error', data:'error occured'})
                    } else {
                        socket.emit('server_result_login_facebook',{type:'success', data:savedUser, token:savedUser.token})
                        logger.info('[success]' + savedUser)
                    }
                })
            } else if(user) {
                user.fbToken = fbAccessToken;
                user.save(function(err, savedUser) {
                    socket.emit('server_result_login_facebook',{type:'success', data:user, token: user.jsonWebToken})
                    logger.info('[success]' + user)
                })
            }
        })
    })

    socket.on('client_signup', function(data) {
        logger.info('[client_signup]'+data)
        let localId = data.id;
        let localPassword = md5(data.password + md5Salt);

        let findConditionLocalUser = {
            id: localId
        }

        userModel.findOne(findConditionLocalUser).exec(function(err, user) {
            if(err) {
                socket.emit('server_result_signup',{type:'error', data:'error occured'})
                logger.info('[error]' + err);
            } else if(user) {
                socket.emit('server_result_signup',{type:'failed', data:'id already exists'})
                logger.info('[failed]' + 'dupulicated id')
            } else if(!user) {
                data.school = 'KAIST'
                localSignup(localId, localPassword, data.name, data.school, function(err, savedUser) {
                    if(err) {
                        socket.emit('server_result_signup',{type:'error', data:'error occured'})
                        logger.info('[error]' + err);
                    } else {
                        socket.emit('server_result_signup',{type:'success', data:savedUser, token:savedUser.jsonWebToken})
                        logger.info('[successs]' + savedUser);
                    }
                })
            }
        })
    })
    
    function localSignup(id, password, name, school, next) {
        let mUserModel = new userModel()
        mUserModel.id = id
        mUserModel.password = password;
        mUserModel.name = name;
        mUserModel.school =school;
        logger.info(userModel)
        mUserModel.save(function(err, newUser) {
            newUser.jsonWebToken = jwt.sign(newUser.id, jwtSecret)
            newUser.save(function(err, savedUser) {
                next(err, savedUser)
            })
        })
    }

    function fbSignup(fbUserId, fbAccessToken , next) {
        let mUserModel = new userModel()
        mUserModel.id = fbUserId
        userModel.fbToken = fbAccessToken
        userModel.save(function(err, newUser) {
            newUser.jsonWebToken = jwt.sign(newUser.id, jwtSecret)
            newUser.save(function(err, savedUser) {
                next(err, savedUser)
            })
        })
    }

    //만약 유저를 특정해야 하는 일이라면 다음과 같이 실행한다.
    socket.on('client_logout', function(data){
            sessionCallback(data, function(user) {
            logger.info('[client_logout]')
            user.jsonWebToken = ""
            user.save()
            socket.disconnect();
        })
    })

    socket.on('client_change_userdata', function(data) {
            sessionCallback(data, function(user) {
            logger.info('[client_change_userdata]'+data)
            user.name = data.name;
            user.school = data.school;
            user.email = data.email;
            user.gender = data.gender;
            user.phone = data.phone;
            user.save(function(err, user) {
                if(err) {
                    socket.emit('server_result_change_userdata', {type:'error'})
                    logger.info('[error]' + err);
                } else {
                    socket.emit("server_result_change_userdata", {type:'success'})
                    logger.info('[successs]')
                }
            })
        })
    })

    socket.on('client_new_group', function(data) {
        sessionCallback(data, function(user) {
            let query = {
                departureDateFrom:data.departureDateFrom,
                departureDateTo:data.departureDateTo,
                destinationLocation:data.destinationLocation,
                departureLocation:data.departureLocation,
            }
            algorithmDataModel.findOne(query).exec(function(err, algo) {
                if(algo) {
                    socket.emit('server_result_new_group', {type:'failed', data:'dupulicated data'})
                    logger.info('failed to adding new group due to dupulicated data')
                } else if(!algo) {
                    let algorithm = new algorithmDataModel();
                    algorithm.id = uuid.v1();
                    algorithm.member = new Array();
                    algorithm.member.push(user.id);
                    algorithm.departureDateFrom = data.departureDateFrom;
                    algorithm.departureDateTo = data.departureDateTo;
                    algorithm.destinationLocation = data.destinationLocation;
                    algorithm.departureLocation = data.departureLocation;
                    console.log(algorithm)
                    algorithm.save(function(err, result) {
                        if(err) {
                            socket.emit('server_result_new_group',{type:'error'})
                            logger.info('error')
                        } else if(result) {
                            socket.emit('server_result_new_group', {type:'success'})
                            logger.info('[successs]' + algorithm);
                        }
                    });
                }
            })
        })
    })

    socket.on('client_join_group', function(data) {
        sessionCallback(data, function(user) {
            let query = {
                id: data.id,
            }
            algorithmDataModel.findOne(query).exec(function(err, algo) {
                if(err) {
                    socket.emit('server_result_join_group', {type:'error'})
                    logger.info('[error]')
                } else {
                    if(!algo) {
                        socket.emit('server_result_join_group',{type:failed,data:'not exist'})
                        logger.info('client ' + user.id + 'tries to join void group failed')
                    } else if(algo.member.length < 4) {
                        algo.member.push(user.id)
                        algo.save()
                        socket.emit('server_result_join_group',{type:'success'})
                        logger.info('client ' + user.id + 'tries to join void group sucess')
                    } else {
                        socket.emit('server_result_join_group',{type:'failed', data:'too many people'})
                    }
                }
            })
        })
    })

    socket.on('client_get_groupinformation', function(data) {
        sessionCallback(data, function(user) {
            let query = {
                id: data.id,
            }
            algorithmDataModel.find(query).exec(function(err, algo) {
                if(err) {
                    socket.emit('server_result_get_groupinformation',{type:'error'})
                    logger.info('[error]')
                } else if(user) {
                    console.log(algo)
                    socket.emit('server_result_get_groupinformation', {type:'success',data:algo})
                    logger.info('[success to send groupinformation]')
                } else {
                    socket.emit('server_result_get_groupinformation',{type:'success',data:'you are not member'})
                    logger.info('[failed] not a memeber')
                }
            })
        })
    })

    socket.on('client_exit_group', function(data) {
        sessionCallback(data, function(user) {
            let query = {
                id:data.id,
            }
            algorithmDataModel.findOne(query).exec(data, function(err, algo) {
                if(err) {
                    socket.emit('server_result_exit_group', {type:'error'})
                    logger.info('[error]')
                } else if(algo) {
                    for(let i=0;i<algo.member.length;i++) {
                        if(algo.member[i] == user.id) {
                            algo.member.remove(i)
                        }
                    }
                    algo.save()
                    socket.emit('server_result_exit_group', {type:'success',data:algo})
                    logger.info('[success]')
                } else {
                    socket.emit('server_result_exit_group',{type:'success',data:'you are not member'})
                    logger.info('[failed] not a memeber')
                }
            })
        })
    })

    //채팅관련 함수들
    socket.on('client_join_chatroom', function(data) { //data에는 만들려는/가입하려는 채팅방의 id = (알고의 id) 가 들어간다
        sessionCallback(data, function(user) {
            let query = {
                id: data.id,
            }
            chatroomModel.findOne(query).exec(function(err, chatroom) {
                let currentDate = new Date();
                let currentMonth = currentDate.getMonth()
                let currentDay = currentDate.getDay()
                let currentHour = currentDate.getHours()
                let currentMin = currentDate.getMinutes()
                let stringDate = currentMonth + '/' + currentDay + ' ' + currentHour +':' + currentMin

                let dupulicated = false;

                if(err) {
                    socket.emit('server_result_join_chatroom', {type:'error'})
                    logger.info('[error]')
                } else if(chatroom) {
                    if(chatroom.message != null) {
                        for(let i=0;i<chatroom.member.length;i++) {
                            if(chatroom.member[i] == user.id) {
                                dupulicated = true;
                            }
                        }
                    }
                    if(!dupulicated) {
                        chatroom.member.push(user.id);
                        //chatroom.message.push({nickname:user.nickname, date:stringDate, message: user.name + '님이 채팅방에 접속하였습니다.'})
                        user.chatroomid = chatroom.id;
                        user.save()
                    }

                    chatroom.save(function(err, result) {
                        if(err) {
                            socket.emit('server_result_join_chatroom', {type:'DB error'})
                            logger.info('[DB error]')
                        } else {
                            socket.emit('server_result_join_chatroom', {type:'success',data:result.id})
                            logger.info('[success]' + 'user ' + user.id + ' join chatroom ' + result.id)
                        }
                    })
                } else if(!chatroom) {
                    let newchatroom = new chatroomModel();
                    newchatroom.id = data.id;
                    newchatroom.message = new Array();
                    newchatroom.member.push(user.id);
                    //newchatroom.message.push({nickname:user.nickname, date:stringDate, message: user.name + '님이 채팅방에 접속하였습니다.'})
                    user.chatroomid = newchatroom.id;
                    user.save()
                    newchatroom.save()
                    newchatroom.save(function(err, result) {
                        if(err) {
                            socket.emit('server_result_join_chatroom', {type:'DB error'})
                            logger.info('[DB error]')
                        } else {
                            socket.emit('server_result_join_chatroom', {type:'success',data:result.id})
                            logger('[success]' + 'user ' + user.id + ' join chatroom ' + result.id)
                        }
                    })
                }
            })
        })
    })

    socket.on('client_exit_chatroom', function(data) {
        sessionCallback(data, function(user) {
            let query = {
                id: data.id,
            }

            chatroomModel.findOne(query).exec(function(err, chatroom) {
                if(err) {
                    socket.emit('server_result_exit_chatroom',{type:'failed',data:'error'})
                    logger.info('DB error')
                } else if(chatroom) {
                    chatroom.member.remove(user.id)
                    user.chatroomid = ""
                    logger.info('success')
                    socket.emit('server_result_exit_chatroom', {type:'success'})
                } else {
                    logger.info('error not a member of the chatroom')
                    socket.emit('server_result_exit_chatroom', {type:'failed', data:'not a member of chatroom'})
                }
            })
        })
    })

    socket.on('client_get_chatroom', function() {
        sessionCallback(data, function(user) {
            let query = {
                member: user.id,
            }

            chatroomModel.find(query).exec(function(err, chatrooms) {
                if(err) {
                    socket.emit('server_result_get_chatroom',{type:'failed',data:'error'})
                    logger.info('DB error')
                } else if(chatrooms) {
                    socket.emit('server_result_get_chatroom',{type:success,data:chatrooms})
                    logger.info('success')
                } else {
                    logger.info('error not a member of the chatroom')
                    socket.emit('server_result_get_chatroom', {type:'failed', data:'not a member of chatroom'})
                }
            })
        })
    })

    socket.on('client_emit_message', function(data) { //data에는 메세지가 들어온다. 
        logger.info('client_emit_message' + data)
        sessionCallback(data, function(user) {
            let query = {
                id: data.id,
            }

            let currentDate = new Date();
            let currentMonth = currentDate.getMonth()
            let currentDay = currentDate.getDay()
            let currentHour = currentDate.getHours()
            let currentMin = currentDate.getMinutes()
            let stringDate = currentMonth + '/' + currentDay + ' ' + currentHour +':' + currentMin

            console.log(stringDate)

            chatroomModel.findOne(query).exec(function(err, chatroom) {
                if(err) {
                    socket.emit('server_result_emit_message',{type:'failed',data:'error'})
                    logger.info('DB error')
                } else if(chatroom) {
                    if(chatroom.message == undefined) {
                        chatroom.message = new Array();
                    }
                    chatroom.message.push({id:user.id, nickname:user.name, date:stringDate, message:data.message});
                    chatroom.index = chatroom.index + 1
                    chatroom.save()
                    socket.emit('server_result_emit_message',{type:'success',data:'chatroom'})
                    logger.info('receive message ' + data.message + ' from ' + user.id)
                } else {
                    socket.emit('server_result_emit_message', {type:'failed',data:'you are not member'})
                    logger.info('failed you are not member')
                }
            })
        })
    })

    socket.on('client_fetch_message', function(data) {
        logger.info('client tryies to fetch messages from server')
        sessionCallback(data, function(user) {
            let query = {
                id: data.id,
            }

            chatroomModel.findOne(query).exec(function(err, chatroom) {
                if(err) {
                    socket.emit('server_result_fetch_message',{type:'failed',data:'error'})
                    logger.info('DB error')
                } else if (chatroom) {
                    chatroom.member = []
                    socket.emit('server_result_fetch_message', {type:'success', data:chatroom})
                    logger.info('success to fetch message')
                } else {
                    socket.emit('server_result_fetch_message', {type:'failed',data:'you are not member'})
                    logger.info('failed you are not member')
                }
            })
        })
    })

    socket.on('client_next_message', function(data) { //data에는 읽을 메세지의  index와 방 번호가 들어온다. 
        let query = {
            id: data.id,
        }

        chatroomModel.findOne(query).exec(function(err, chatroom) {
            if(err) {
                socket.emit('server_result_next_message',{type:'failed',data:'error'})
                logger.info('DB error')
            } else if(chatroom) {
                if(data.index == null) {
                    logger.info('failed invalid input')
                    socket.emit('server_result_next_message', {type:'failed', data:'invalid input'})
                } else if(chatroom.message.length < data.index) {
                    socket.emit('server_result_next_message', {type:'failed', data:'index out of range'})
                } else if (chatroom.message.length >= data.index + 1) {
                    let result = new Array();
                    for(let i = data.index + 1; i <= chatroom.message.length; i++) {
                        if(chatroom.message[i] != null && chatroom.message[i] != []) {
                            result.push(chatroom.message[i]);
                        }
                    }
                    if(result.length != 0) {
                        console.log(result);
                        socket.emit('server_result_next_message', {type:'success', data:result})
                        logger.info('success to get next message')
                    } else {
                        socket.emit('server_result_next_message', {type:'failed', data:'index out of range'})
                    }
                } else {
                    socket.emit('server_result_next_message', {type:'failed', data:'you are not member of this chatroom'})
                    logger.info('failed to get room data')
                }
            }
        })
    })

    function sessionCallback(data, next) {
        let findConditionToken = {
            jsonWebToken: data.token
        }
        userModel.findOne(findConditionToken).exec(function(err, user) {
            if(err) {
                socket.emit('server_result_check_session',{type:'error', data:'error occured'})
                logger.info('[error]' + err)
            } else if(!user) {
                socket.emit('server_result_check_session',{type:'failed',data:'token not founded'})
                logger.info('[failed]' + 'token not founded')
            }
            else if(user) {
                logger.info('[success to find session user] ' + user.id)
                next(user);
            }
        })
    }

    //먄약 유저를 특정하지 않으면 다음과 같이 실행한다.
    socket.on('client_disconnect', function(data) {
        logger.info('socket ' + socket.id + ' disconnected')
        socket.disconnect();
    })

    socket.on('client_get_algorithmdata', function(data) {
        algorithmDataModel.find({}, function(err, result) {
            if(err) {
                socket.emit('server_result_get_algorithmdata', {type:'error'});
                logger.info('DB error')
            } else {
                socket.emit('server_result_get_algorithmdata', {type:'success',data:result});
                logger.info('Success to send information')
            }
        })
    })
})






