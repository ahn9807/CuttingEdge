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
})
const algorithmDataSchema = mongoose.Schema({
    id:'String',
    member:[String],
    departureDateFrom:'String',
    departureDateTo:'String',
    departureLocation:'String',
    destincationLocation:'String',
})
const chatroomSchema = mongoose.Schema({
    id:'String',
    member:[String], //id of each memebers
    message:[{nickname:String, date:{type:Date, default:Date.now}, message:'String'}]
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
                socket.emit('server_result',{type:'error', data:'error occured'})
                logger.info('[error]' + 'DB Not found')
            } else if(!user) {
                socket.emit('server_result',{type:'failed', data:'Incorrect id/password'})
                logger.info('[failed]Incorrect id/password')
            } else if(user) {
                socket.emit('server_result',{type:'success', data:user, token:user.jsonWebToken})
                logger.info('[success]' + user.jsonWebToken)
            }
        })
    })

    socket.on('client_check_duplicate', function(data) {
        logger.info('[client_check_cuplicate]')
        
        let findConditionLocalUser = {
            id: data.id,
        }

        userModel.findOne(findConditionLocalUser).exec(function(err, user) {
            if(err) {
                socket.emit('server_result',{type:'error', data:'error occured'})
                logger.info('[error]' + 'DB Not found')
            } else if(!user) {
                socket.emit('server_result',{type:'success', data:'id not exists'})
                logger.info('[success]')
            } else {
                socket.emit('server_result',{type:'failed', data:'id already exists'})
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
                socket.emit('server_result',{type:'error', data:'error occured'})
            } else if(!user) {
                fbSignup(fbUserId, fbAccessToken, function(err, savedUser) {
                    if(err) {
                        socket.emit('server_result',{type:'error', data:'error occured'})
                    } else {
                        socket.emit('server_result',{type:'success', data:savedUser, token:savedUser.token})
                        logger.info('[success]' + savedUser)
                    }
                })
            } else if(user) {
                user.fbToken = fbAccessToken;
                user.save(function(err, savedUser) {
                    socket.emit('server_result',{type:'success', data:user, token: user.jsonWebToken})
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
                socket.emit('server_result',{type:'error', data:'error occured'})
                logger.info('[error]' + err);
            } else if(user) {
                socket.emit('server_result',{type:'failed', data:'id already exists'})
                logger.info('[failed]' + 'dupulicated id')
            } else if(!user) {
                localSignup(localId, localPassword, function(err, savedUser) {
                    if(err) {
                        socket.emit('server_result',{type:'error', data:'error occured'})
                        logger.info('[error]' + err);
                    } else {
                        socket.emit('server_result',{type:'success', data:savedUser, token:savedUser.jsonWebToken})
                        logger.info('[successs]' + savedUser);
                    }
                })
            }
        })
    })
    
    function localSignup(id, password, next) {
        let mUserModel = new userModel()
        mUserModel.id = id
        mUserModel.password = password;
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
                    socket.emit('server_result', {type:'error'})
                    logger.info('[error]' + err);
                } else {
                    socket.emit("server_result", {type:'success'})
                    logger.info('[successs]')
                }
            })
        })
    })

    socket.on('client_new_group', function(data) {
        sessionCallback(data, function(user) {
            algorithmDataModel.find(data).exec(function(err, algo) {
                if(algo) {
                    socket.emit('server_result', {type:'failed', data:'dupulicated data'})
                    logger.info('failed to adding new group due to dupulicated data')
                } else if(!algo) {
                    let algorithm = new algorithmDataModel();
                    algorithm.id = uuid.v1();
                    algorithm.member = new Array().push(user.id);
                    algorithm.departureDateFrom = data.departureDateFrom;
                    algorithm.departureDateTo = data.departureDateTo;
                    algorithm.destincationLocation = data.destincationLocation;
                    algorithm.departureLocation = data.departureLocation;
                    algorithm.save(function(err, result) {
                        if(err) {
                            socket.emit('server_result',{type:'error'})
                            logger.info('error')
                        } else if(result) {
                            socket.emit('server_result', {type:'success'})
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
                    socket.emit('server_result', {key:'error'})
                    logger.info('[error]')
                } else {
                    if(!algo) {
                        socket.emit('server_result',{key:failed,data:'not exist'})
                        logger.info('client ' + user.id + 'tries to join void group')
                    } else if(algo.member.length < 4 || l) {
                        algo.member.push(user.id)
                        socket.emit('server_result',{key:'success'})
                    } else {
                        socket.emit('server_result',{key:'failed', data:'too many people'})
                    }
                }
            })
        })
    })

    socket.on('client_get_groupinformation', function(data) {
        sessionCallback(data, function(user) {
            let query = {
                id: user.id,
            }
            algorithmDataModel.findOne(query).exec(function(err, algo) {
                algo.member = new Array()
                if(err) {
                    socket.emit('server_result', {key:'error'})
                    logger.info('[error]')
                } else if(user) {
                    socket.emit('server_result', {key:'success',data:algo})
                    logger.info('[success]')
                } else {
                    socket.emit('server_result',{key:'success',data:'you are not member'})
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
                    socket.emit('server_result', {key:'error'})
                    logger.info('[error]')
                } else if(algo) {
                    for(let i=0;i<algo.member.length;i++) {
                        if(algo.member[i] == user.id) {
                            algo.member.remove(i)
                        }
                    }
                    algo.save()
                    socket.emit('server_result', {key:'success',data:algo})
                    logger.info('[success]')
                } else {
                    socket.emit('server_result',{key:'success',data:'you are not member'})
                    logger.info('[failed] not a memeber')
                }
            })
        })
    })

    //채팅관련 함수들
    socket.on('client_join_chatroom', function(data) { //data에는 만들려는/가입하려는 채팅방의 id = (알고의 id) 가 들어간다
        sessionCallback(data, function(user) {
            let query = {
                id:data.id,
            }
            chatroomSchema.find(query).exec(function(err, chatroom) {
                if(err) {
                    socket.emit('server_result', {key:'error'})
                    logger.info('[error]')
                } else if(chatroom) {
                    chatroom.member.push(user.id);
                    chatroom.nickname.push(user.nickname)
                    chatroom.message.push({nickname:user.nickname, date:Date.now, message:user.nickname + '님이 채팅방에 접속하였습니다.'})
                    chatroom.save(function(err, result) {
                        if(err) {
                            socket.emit('server_result', {key:'DB error'})
                            logger.info('[DB error]')
                        } else {
                            socket.emit('server_result', {key:'success',data:result.id})
                            logger('[success]' + 'user ' + user.id + ' join chatroom ' + result.id)
                        }
                    })
                } else if(!chatroom) {
                    let newchatroom = new chatroomModel();
                    newchatroom.id = data.id;
                    newchatroom.member.push(user.id);
                    newchatroom.nickname.push(user.nickname)
                    newchatroom.message.push({nickname:user.nickname, date:Date.now, message:user.nickname + '님이 채팅방에 접속하였습니다.'})
                    newchatroom.save()
                    newchatroom.save(function(err, result) {
                        if(err) {
                            socket.emit('server_result', {key:'DB error'})
                            logger.info('[DB error]')
                        } else {
                            socket.emit('server_result', {key:'success',data:result.id})
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
        })
    })

    function sessionCallback(data, next) {
        let findConditionToken = {
            jsonWebToken: data.token
        }
        userModel.findOne(findConditionToken).exec(function(err, user) {
            if(err) {
                socket.emit('server_result',{type:'error', data:'error occured'})
                logger.info('[error]' + err)
            } else if(!user) {
                socket.emit('server_result',{type:'failed',data:'token not founded'})
                logger.info('[failed]' + 'token not founded')
            }
            else if(user) {
                socket.emit('server_result',{type:'success', data:user})
                logger.info('[success]' + user)
                next(user);
            }
        })
    }

    //먄약 유저를 특정하지 않으면 다음과 같이 실행한다.
    socket.on('client_disconnect', function(data) {
        socket.disconnect();
    })

    socket.on('client_get_algorithmdata', function(data) {
        algorithmDataModel.find({}, function(err, result) {
            if(err) {
                socket.emit('server_result', {type:'error'});
                logger.info('DB error')
            } else {
                socket.emit('server_result', {type:'success',data:result});
                logger.info('Success to send information')
            }
        })
    })
})






