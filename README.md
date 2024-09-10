# def-data

Describe data with malli and get accessor def's generated automatically.

NOTE: Only works for Clojurescript currently. I intend to use this for re-frame db and iterate from there. Still early phases and subject to change.

## Installation

TODO Clojars publish


## Examples

``` clojure
(def-data name base-key
   [:map
     [:id :int]
     [:name :string]])

 ->>>

  (def name
    [:map
     [:id :int]
     [:name :string]])
 
  (def name->id [:base-key :id])
  (def name->name [:base-key :name])

```

### Advantage
Accessors will always match the schema structure. If schema changes def's wont exist and with lsp support editor will complain all the locations where previous def's were used.
  
### Limitations
Does not support predicate macros in schema. For example `string?`.


## License

Copyright © 2024 Lari Saukkonen

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
