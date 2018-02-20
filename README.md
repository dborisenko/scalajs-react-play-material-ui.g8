# Client-server application template with Scala.js, React, Material-UI and Play

With this template you can easily start to develop client-server application with *Scala.js*, *React* and *Material-UI* on the client side and *Play* on the server side. *Circe* is used for serialization of data between client and server. 

## Architecture
 
The following technologies are used:

* For front-end side (`client` module)
  * [Scala.js](https://www.scala-js.org/) v0.6.21
  * [scalajs-react](https://github.com/japgolly/scalajs-react) v1.1.1
  * [scalajs-react-components](http://chandu0101.github.io/sjrc/) v0.8.0
  * [react](https://reactjs.org/) v15.6.1
  * [material-ui](http://www.material-ui.com/) v0.20.0
* For back-end side (`server` module)
  * [Play Framework](https://www.playframework.com/) v2.6.11
* For client-server messages and shared code cross-compiled to both jvm and js (`shared` module)
  * [Circe](https://circe.github.io/circe/) v0.9.0

## Usage

```bash
$ sbt new dborisenko/scalajs-react-play-material-ui.g8
```
Done! just relax and start your code!

## Contributing
1. Fork it!
2. Create your feature branch: `git checkout -b my-new-feature`
3. Commit your changes: `git commit -am 'Add some feature'`
4. Push to the branch: `git push origin my-new-feature`
5. Submit a pull request :D
