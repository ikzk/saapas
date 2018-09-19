# TODO

- [ ] Swagger-API
- [ ] mongodb

# Saapas

<img src="saapas.png" align="right">

Opinionated example project for ClojureScript using Boot instead of Leiningen.
Was inspired by [chestnut] but has grown since to include other stuff to
demonstrate Boot.

This is not an Leiningen template as I don't believe in setting up complex
projects automatically. Instead you should study this project and copy
only stuff you need and understand.

### Prerequisites

You should first [install Boot][install]. Also you should be running the
latest version.

## Features

- Scss
  - You can add dependency to e.g. bootstrap from [webjars] to
    your regular dependencies and then use `@import "bootstrap/scss/bootstrap.scss"`
    on your `.scss` files.
- `dev` task starts the whole development workflow
  - [Browser repl][boot-cljs-repl] included
  - Watches for file changes
  - Live-reloading using boot-figreload
- `autotest` task to run Clj and Cljs tests whenever files are changed
  - Uses [boot-bat-test] to run Clojure tests as fast as possible and only running the changed tests are file changes
  - Uses [boot-cljs-test] to run ClojureScript tests with [Doo], allowing the tests to run in many of JS environments, like browsers, Node or PhantomJS.
- Provides `package` task that creates Uberjar that can be used to run the app on a server
  - Cljs will be compiled using `:advanced` optimization and uses minified foreign libraries, like React
  - Only [`backend.main`](./src/clj/backend/main.clj) is AOT compiled, so that it is possible to start the application with `java -jar saapas.jar` but so that rest of the code is compiled at application startup to avoid problems with AOT compilation.

## License

GNU AFFERO GENERAL PUBLIC LICENSE.

[chestnut]: https://github.com/plexus/chestnut
[install]: https://github.com/boot-clj/boot#install
[component]: https://github.com/stuartsierra/component
[reloaded.repl]: https://github.com/weavejester/reloaded.repl
[compojure]: https://github.com/weavejester/compojure
[reagent]: https://github.com/reagent-project/reagent
[LESS]: http://lesscss.org/
[sass]: http://sass-lang.com/
[less4clj]: https://github.com/Deraen/less4clj
[sass4clj]: https://github.com/Deraen/sass4clj
[webjars]: http://www.webjars.org
[boot-cljs]: https://github.com/boot-clj/boot-cljs
[boot-cljs-repl]: https://github.com/adzerk/boot-cljs-repl
[boot-reload]: https://github.com/adzerk/boot-reload
[boot-alt-test]: https://github.com/metosin/boot-alt-test
[boot-cljs-test]: https://github.com/crisptrutski/boot-cljs-test
[Doo]: https://github.com/bensu/doo
