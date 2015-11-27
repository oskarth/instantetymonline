# instantetymonline

I wanted to make the https://instantdomainsearch.com version of
http://etymonline.com/.

~10mb minified for 40k words including definitions.

See http://experiments.oskarth.com/etym for more.

## Deploy

`./scripts/release`
`./scripts/deploy`

## Development

Hook in and run `(start!)` and `(handle-events!)` in a Clojure REPL. Separately, start a shell and run `lein figwheel` for Clojurescript browser repl.

Go to http://localhost:3449/ to connect to the Clojurescript REPL.

Gotcha: Something something auto-loading. If you do the above and run `(chsk-send! [:some/text "foo"])` in Figwheel it works.

## TODO

- Fuzzy substring matching?
- Nicer CSS?
- Autocomplete? (https://github.com/reagent-project/reagent-cookbook/blob/master/recipes/autocomplete/src/cljs/autocomplete/core.cljs)

## License

Copyright © 2015 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
