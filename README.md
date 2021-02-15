<p align="center">
    <img src="https://i.imgur.com/bADycH9.png" alt="lightbulb logo" />
</p>

# Stream Lights

This is a java based software package which can trigger Philips Hue lights when an event occurs on your Twitch stream.

## Getting Started

To get started using this software package there are a few simple steps:

- Gather the required access tokens, keys, and credentials to access the Twitch and Philips hue API's (we will walk you through this!)
- Start the server (in this software package) and create some subscriptions to your (or who ever's) Twitch account
- Configure your lights to take certain actions when a new event is received

## Prerequisites

This section outlines the process of gathering the required tokens, keys, and credentials to use this software. Its broken
down into two sections: Twitch Setup and Philips Hue Setup.

#### Twitch Setup 
In order to run the server and connect to Twitch you will need some credentials.

Create a new [Twitch developer App here](https://dev.twitch.tv/console/apps/create) with the following properties:

| Name               | OAuth Redirect URL | Category     |
|--------------------|--------------------|--------------|
| Any name you want! | http://localhost   | Any category |

![image twitch app creation](https://i.imgur.com/UvFMKLn.png)

Once your app is created make sure you copy your Client Id and generate (and copy) a new Client Secret. You will need
to keep these safe and in a private place (Stream lights will need this later).

![image twitch app registration](https://i.imgur.com/pL6PfSR.png)

#### Philips Hue Setup

This software requires that you have a registered token with your Philips Hue Bridge.
Philips Hue has [excellent documentation here](https://developers.meethue.com/develop/get-started-2/) which will walk you through this process.

An example token looks like this: `1028d66426293e821ecfd9ef1a0731df`.

To summarize the Philips Hue documentation you need to:

- Discover the IP address of your Hue Bridge at [Hue Discovery](https://discovery.meethue.com/)
- Go to the CLIP configuration panel in a Web Browser (Google Chrome) within your Bridge at http://<YOUR_BRIDGE_IP>/debug/clip.html
- Put `/api` in the URL box and `{"devicetype":"stream_lights#stream_server"}`
- Click the button on the top of your Hue
- Sprint back to your computer!!
- Click the "Post" button in the CLIP configuration panel
- Copy the randomly generated username from the Command Response field.

![hue_setup](https://i.imgur.com/2lo5MMQ.png)

### Installing

To install this software you can either download the latest jar from the [releases](https://github.com/cbartram/Stream-Lights/releases) section or clone this repository with

```shell
$ git clone https://github.com/cbartram/Stream-Lights.git
```

and build the jar file locally using [Maven](https://maven.apache.org).

```shell
$ mvn clean package
```

### Running

To run the server you should have [Java](https://www.java.com/en/download/) installed on your local machine. This software is packaged
into a runnable Java archive which exposes a webserver on your local machine. You can run the server using:

```shell
$ export TWITCH_CLIENT_ID=<Your twitch client id from previous step>
$ export TWITCH_CLIENT_SECRET=<Your twitch client secret from previous step>
$ java -jar /path/to/the/StreamLights-0.0.1-SNAPSHOT.jar
```

The server will be running on your local machine at `http://localhost:8080` we recommend installing [ngrok](https://ngrok.com/) to expose 
this server to the internet through an HTTPS domain. Having an https domain is required to register a callback function for Twitch integration. 

To use [ngrok](https://ngrok.com/) simply follow its documentation to download the executable file and run: 

```shell
$ ngrok 8080
```

You should see output which looks like this:

```shell
Session Status                online
Account                       Your Account (Plan: Free)
Version                       2.3.35
Region                        United States (us)
Web Interface                 http://127.0.0.1:4040
Forwarding                    http://5c69b7.ngrok.io -> http://localhost:8080
Forwarding                    https://5c69b7.ngrok.io -> http://localhost:8080

Connections                   ttl     opn     rt1     rt5     p50     p90
                              91      0       0.00    0.00    0.01    60.23

HTTP Requests
-------------
```

Grab the `Forwarding` https address to use for your callback URL in the subscription registration payload. You can test
that everything is setup correctly by using the following shell command:

```shell
curl -X GET http://localhost:8080/actuator/health 
# {
#  "status": "UP"
# }
```

If you used `ngrok` then make sure to check your `ngrok` Forwarding URL with the `curl` command!

### Creating a Subscription

You can create a subscription through the `ngrok` endpoint or your `localhost`. Creating a subscription will let you hook into
many different types of Twitch events to trigger your Philips hue lights.

Please reference the [Twitch Documentation](https://dev.twitch.tv/docs/eventsub/eventsub-subscription-types) for a list of viable events to which 
you can subscribe.

Creating a subscription can be done with the following Curl request:

```shell
curl --location --request POST 'http://localhost:8080/twitch/subscription' \
--header 'Content-Type: application/json' \
--data-raw '{
    "type": "channel.follow",
    "condition": {
        "username": "runewraith_"
    },
    "transport": {
        "callback": "https://5c679b7.ngrok.io/twitch/subscription/webhook"
    }
}'
```

Take note that this request is essentially saying "Notify my Philips hue lights anytime the user: runewraith_ on Twitch get a new follower to his/her channel."
Your transport callback **must use HTTPS** therefore it should be set to your `ngrok` forwarding URL. 

Some other common events you may want to subscribe to include: 

- `channel.subscribe`
- `channel.cheer`
- `channel.hype_train.begin`
- `channel.hype_train.progress`
- `channel.hype_train.end`
- `stream.online`

### Listing Subscriptions

You can list all your current subscriptions with the following curl: 

```shell
curl --location --request GET 'http://localhost:8080/twitch/subscription' \
--header 'Content-Type: application/json'
```

It will return a response which looks like the following:

```json
{
    "total": 1,
    "data": [
        {
            "id": "9e5d41-269-402-a96-afacde85b",
            "status": "enabled",
            "type": "channel.follow",
            "version": "1",
            "condition": {
                "broadcaster_user_id": "123456"
            },
            "created_at": "2021-02-07T17:38:20.799440479Z",
            "transport": {
                "method": "webhook",
                "callback": "https://5c6759b7.ngrok.io/twitch/subscription/webhook"
            }
        }
    ],
    "limit": 10000,
    "pagination": {}
}
```

### Removing an existing Subscription

You can remove an existing subscription as long as you have the subscription id. The subscription id is a long string 
which will look something like this: `"9e5d41-269-402-a96-afacde85b"`. Your subscription id is returned in the response
when you create a new subscription.

The following curl will delete a subscription ID called `<SUBSCRIPTION_ID_HERE>`

```shell
curl --location --request DELETE 'http://localhost:8080/twitch/subscription/<SUBSCRIPTION_ID_HERE>' \
--header 'Content-Type: application/json'
```

The response will not contain a body but will have a 200 status code.

## Running the tests

Tests are written and run with JUnit and Mockito. To run the tests simply use [Maven](https://maven.apache.org):

```shell
$ mvn clean test
```

## Deployment

Deployment section coming soon! 

## Built With

* [Spring](http://spring.io/) - The web framework used
* [Maven](https://maven.apache.org/) - Dependency Management and build system
* [Java](https://java.com) - Programming Language

## Contributing

Please read [CONTRIBUTING.md](https://gist.github.com/PurpleBooth/b24679402957c63ec426) for details on our code of conduct, and the process for submitting pull requests to us.

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/cbartram/Stream-Lights/tags).

## Authors

* **Christian Bartram** - *Initial work* - [Cbartram](https://github.com/cbartram)

See also the list of [contributors](https://github.com/cbartram/Stream-Lights/contributors) who participated in this project.

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

## Acknowledgments

* Twitch for making a simple and safe API
* Philips hue for making some fun hackable lights!


