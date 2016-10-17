/**
 * this node server is used for enabling group chat functionality in the client webapp.
 *
 * it uses: socket.io
 *
 * server ports:
 *    express server
 *        dev: port 3333
 *        prod: port 80
 */

const GLOBAL_CONSTANTS = require('../../global/constants').GLOBAL_CONSTANTS;

/**
 * get the NODE_ENV environment var value from node -
 * can be null, or set to "production", or "development"
 */
const NODE_ENV = process.env.NODE_ENV;
const lodash = require("lodash");

let isProduction = false;
if (!lodash.isNil(NODE_ENV)) {
  if (lodash.isEqual(NODE_ENV, "production")) isProduction = true;
}

console.log("**** Server isProduction: " + isProduction + " ****");

/**
 * socket.io
 * more info
 * - https://nodesource.com/blog/understanding-socketio/
 * - https://devcenter.heroku.com/articles/node-websockets
 */

let socketio;

/**
 * start server on port 8080 if in DEV
 */
if (!isProduction) {
  let http = require('http');
  let httpServer = http.createServer(
    (req, res)=> {
      res.writeHead(200, {"Content-Type": "text/html"});
      res.end("<h1>Socket IO Server Running</h1>");
    }
  );
  httpServer.listen(8080);
  let io = require("socket.io");
  socketio = io(httpServer);
}

/**
 * express server for production mode
 * more info
 * - webpack + express: https://youtu.be/cdUyEou0LHg
 * - webpack + express: https://youtu.be/Ru3Rj_hM8bo
 * - node env vars: http://goo.gl/k4mFC8
 */
if (isProduction) {
  const express = require("express");
  const path = require("path");
  const port = process.env.PORT || 3333;
  const app = express();
  
  let root_dir = __dirname + '/../static_content/';
  
  app.use(express.static(root_dir));
  
  app.get(
    "*", (req, res)=> {
      res.sendFile(path.resolve(root_dir, "index.html"));
    }
  );
  
  let httpServer = app.listen(port);
  
  console.log("Production Server started on port: " + port);
  
  let io = require("socket.io");
  socketio = io.listen(httpServer);
}

/**
 * socket.io
 */
if (!lodash.isNil(socketio))
  socketio.on(
    "connection",
    (socket)=> {
      
      socket.on(
        GLOBAL_CONSTANTS.REMOTE_MESSAGE_FROM_CLIENT,
        (data)=> {
          console.log("Received message from client: " + JSON.stringify(data));
          // socket.broadcast.emit(GLOBAL_CONSTANTS.REMOTE_MESSAGE_FROM_SERVER, data);
          // socket.emit(GLOBAL_CONSTANTS.REMOTE_MESSAGE_FROM_SERVER, data);
          socketio.sockets.emit(GLOBAL_CONSTANTS.REMOTE_MESSAGE_FROM_SERVER, data)
        }
      );
      
      socket.on(
        "disconnect",
        ()=> {
          console.log("Socket has disconnected");
        }
      )
      
    }
  );
