// "use strict";

let AWS = require("aws-sdk");
let polly = new AWS.Polly();
let s3 = new AWS.S3();
const uuidv1 = require('uuid/v1');
module.exports.speak = (event, context, callback) => {
  let data = JSON.parse(event.body);
  const pollyParams = {
    OutputFormat: "mp3",
    Text: data.text,
    VoiceId: data.voice
  };
  // 1. Getting the audio stream for the text that user entered
  polly.synthesizeSpeech(pollyParams)
    .on("success", function (response) {
      let data = response.data;
      let audioStream = data.AudioStream;
      let key = uuidv1();
      let s3BucketName = 'my-talking-aider-app';  
      // 2. Saving the audio stream to S3
      let params = {
        Bucket: s3BucketName,
        Key: key + '.mp3',
        Body: audioStream
      };
      s3.putObject(params)
        .on("success", function (response) {
          console.log("S3 Put Success!");
        })
        .on("complete", function () {
          console.log("S3 Put Complete!");
          let s3params = {
            Bucket: s3BucketName,
            Key: key + '.mp3',
          };
          // 3. Getting a signed URL for the saved mp3 file 
          let url = s3.getSignedUrl("getObject", s3params);
          // Sending the result back to the user
          let result = {
            bucket: s3BucketName,
            key: key + '.mp3',
            url: url
          };
          callback(null, {
            statusCode: 200,
            headers: {
              "Access-Control-Allow-Origin" : "*",
              'Access-Control-Allow-Credentials': true
            },
            body: JSON.stringify(result)
          });
        })
        .on("error", function (response) {
          console.log(response);
        })
        .send();
    })
    .on("error", function (err) {
      callback(null, {
        statusCode: 500,
        headers: {
          "Access-Control-Allow-Origin" : "*",
          'Access-Control-Allow-Credentials': true
        },
        body: JSON.stringify(err)
      });
    })
    .send();
};

// module.exports.hello = async (event) => {
//   return {
//     statusCode: 200,
//     body: JSON.stringify(
//       {
//         message: "Go Serverless v2.0! Your function executed successfully!",
//         input: event,
//       },
//       null,
//       2
//     ),
//   };
// };
