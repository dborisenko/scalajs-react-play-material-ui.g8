# Client-server application template with Scala.js, React, Material-UI and Play

With this template you can easily start to develop client-server application with *Scala.js*, *React* and *Material-UI* on the client side and *Play* on the server side. *Circe* is used for serialization of data between client and server. 

Template is implemented in the form of TODO application example.

## Architecture
 
The following technologies are used:

* For front-end side (`client` module)
  * [Scala.js](https://www.scala-js.org/) v0.6.21
  * [scalajs-react](https://github.com/japgolly/scalajs-react) v1.1.1
  * [scalajs-react-components](http://chandu0101.github.io/sjrc/) v0.8.0
  * [react](https://reactjs.org/) v15.6.1
  * [material-ui](http://www.material-ui.com/) v0.20.0
  * [npm](https://www.npmjs.com/) is used for building JavaScript dependencies
* For back-end side (`server` module)
  * [Play Framework](https://www.playframework.com/) v2.6.11
* For client-server messages and shared code cross-compiled to both jvm and js (`shared` module)
  * [Circe](https://circe.github.io/circe/) v0.9.0

## Usage

Make sure that you have `npm` and `sbt` installed.

```bash
$ sbt new dborisenko/scalajs-react-play-material-ui.g8
```

Now you can run your application
```bash
$ sbt server/run
```

And now TODO application example is accessible in [localhost:9000](http://localhost:9000/)

## Contributing
1. Fork it!
2. Create your feature branch: `git checkout -b my-new-feature`
3. Commit your changes: `git commit -am 'Add some feature'`
4. Push to the branch: `git push origin my-new-feature`
5. Submit a pull request :D
