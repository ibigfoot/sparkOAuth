#Tomcat Template

A simple java web app built with Spark and Moustache configured for [Heroku](www.heroku.com) and local development.
Have a look at how to configure a [ConnectedApp](https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/intro_defining_remote_access_applications.htm) in Salesforce.

#Installation -

Assumes you have configured your local environment with the [Heroku Toolbelt](https://toolbelt.heroku.com/)

##Run locally

Run locally from the commandline with

    // TODO

Or 
Run directly from Eclipse 

- Run->External Tools->External Tool Configurations
- Add new program
- Set Location to your java executible (for my mac os x this was at /usr/bin/java)
- Set Working directory to your project root
- Go the environment tab and set the OAuth parameters 
    - client_id
    - client_secret
    - redirect_uri
- optionally, set -Dorg.slf4j.simpleLogger.defaultLogLevel

##Run Heroku

Here is a friendly Heroku button... Once deployed you will need to update your config vars to point to use the OAuth params in your IdP 

[![Deploy](https://www.herokucdn.com/deploy/button.png)](https://heroku.com/deploy)