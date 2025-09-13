# Duct Handler [![Build Status](https://github.com/duct-framework/handler/actions/workflows/test.yml/badge.svg)](https://github.com/duct-framework/handler/actions/workflows/test.yml)

A library that provides common [Ring][] handlers for the [Duct][]
framework.

[ring]: https://github.com/ring-clojure/ring
[duct]: https://github.com/duct-framework/duct

## Installation

Add the following dependency to your deps.edn file:

    org.duct-framework/handler {:mvn/version "0.1.2"}

Or to your Leiningen project file:

    [org.duct-framework/handler "0.1.2"]

## Usage

This library provides three [Integrant][] keys that initiate into Ring
handlers.

`:duct.handler/static` creates a handler that always returns the same
response map. The value of this key is the response map itself.

`:duct.handler/file` and `:duct.handler/resource` are handlers that
return files or resources. They take two options:

- `:paths` - a map of paths strings to an option map
- `:not-found` - a response map to be returned if no path matches

If the `:not-found` option is not supplied, `nil` is returned from the
handler (which can be useful for trying multiple handlers to see which
matches).

The options mapped from each path go directly to the
[ring.util.reponse/file-response][file-resp] and
[ring.util.response/resource-response][res-resp] functions.

For example:

```edn
{:duct.handler/file
 {"/" {:root "public"}
  "/js" {:root "target/js"}}}
```

[integrant]: https://github.com/weavejester/integrant
[file-resp]: https://ring-clojure.github.io/ring/ring.util.response.html#var-file-response
[res-resp]: https://ring-clojure.github.io/ring/ring.util.response.html#var-resource-response

## License

Copyright © 2025 James Reeves

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
