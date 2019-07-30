(ns crux-bench.watdiv.datomic-schema)


;; See: https://dsg.uwaterloo.ca/watdiv/watdiv-data-model.txt
;; Some things like dates are strings in the actual data.
(def datomic-watdiv-schema
  [#:db{:ident (keyword "http://db.uwaterloo.ca/~galuc/wsdbm/composer")
        :cardinality :db.cardinality/one
        :valueType :db.type/string}
   #:db{:ident (keyword "http://db.uwaterloo.ca/~galuc/wsdbm/follows")
        :cardinality :db.cardinality/many
        :valueType :db.type/ref}
   #:db{:ident (keyword "http://db.uwaterloo.ca/~galuc/wsdbm/friendOf")
        :valueType :db.type/ref
        :cardinality :db.cardinality/many}
   #:db{:ident (keyword "http://db.uwaterloo.ca/~galuc/wsdbm/gender")
        :valueType :db.type/ref
        :cardinality :db.cardinality/one}
   #:db{:ident (keyword "http://db.uwaterloo.ca/~galuc/wsdbm/hasGenre")
        :valueType :db.type/ref
        :cardinality :db.cardinality/many}
   #:db{:ident (keyword "http://db.uwaterloo.ca/~galuc/wsdbm/hits")
        :valueType :db.type/long
        :cardinality :db.cardinality/one}
   #:db{:ident (keyword "http://db.uwaterloo.ca/~galuc/wsdbm/likes")
        :valueType :db.type/ref
        :cardinality :db.cardinality/many}
   #:db{:ident (keyword "http://db.uwaterloo.ca/~galuc/wsdbm/makesPurchase")
        :valueType :db.type/ref
        :cardinality :db.cardinality/many}
   #:db{:ident (keyword "http://db.uwaterloo.ca/~galuc/wsdbm/purchaseDate")
        :valueType :db.type/string
        :cardinality :db.cardinality/one}
   #:db{:ident (keyword "http://db.uwaterloo.ca/~galuc/wsdbm/purchaseFor")
        :valueType :db.type/ref
        :cardinality :db.cardinality/one}
   #:db{:ident (keyword "http://db.uwaterloo.ca/~galuc/wsdbm/subscribes")
        :valueType :db.type/ref
        :cardinality :db.cardinality/many}
   #:db{:ident (keyword "http://db.uwaterloo.ca/~galuc/wsdbm/userId")
        :valueType :db.type/long
        :cardinality :db.cardinality/one}
   #:db{:ident (keyword "http://ogp.me/ns#tag")
        :valueType :db.type/ref
        :cardinality :db.cardinality/many}
   #:db{:ident (keyword "http://ogp.me/ns#title")
        :valueType :db.type/string
        :cardinality :db.cardinality/one}
   #:db{:ident (keyword "http://purl.org/dc/terms/Location")
        :valueType :db.type/ref
        :cardinality :db.cardinality/one}
   #:db{:ident (keyword "http://purl.org/goodrelations/description")
        :valueType :db.type/string
        :cardinality :db.cardinality/one}
   #:db{:ident (keyword "http://purl.org/goodrelations/includes")
        :valueType :db.type/ref
        :cardinality :db.cardinality/one}
   #:db{:ident (keyword "http://purl.org/goodrelations/name")
        :valueType :db.type/string
        :cardinality :db.cardinality/one}
   #:db{:ident (keyword "http://purl.org/goodrelations/offers")
        :valueType :db.type/ref
        :cardinality :db.cardinality/many}
   #:db{:ident (keyword "http://purl.org/goodrelations/price")
        :valueType :db.type/long
        :cardinality :db.cardinality/one}
   #:db{:ident (keyword "http://purl.org/goodrelations/serialNumber")
        :valueType :db.type/long
        :cardinality :db.cardinality/one}
   #:db{:ident (keyword "http://purl.org/goodrelations/validFrom")
        :valueType :db.type/string
        :cardinality :db.cardinality/one}
   #:db{:ident (keyword "http://purl.org/goodrelations/validThrough")
        :valueType :db.type/string
        :cardinality :db.cardinality/one}
   #:db{:ident (keyword "http://purl.org/ontology/mo/artist")
        :valueType :db.type/ref
        :cardinality :db.cardinality/one}
   #:db{:ident (keyword "http://purl.org/ontology/mo/conductor")
        :valueType :db.type/ref
        :cardinality :db.cardinality/one}
   #:db{:ident (keyword "http://purl.org/ontology/mo/movement")
        :valueType :db.type/long
        :cardinality :db.cardinality/one}
   #:db{:ident (keyword "http://purl.org/ontology/mo/opus")
        :valueType :db.type/long
        :cardinality :db.cardinality/one}
   #:db{:ident (keyword "http://purl.org/ontology/mo/performed_in")
        :valueType :db.type/ref
        :cardinality :db.cardinality/one}
   #:db{:ident (keyword "http://purl.org/ontology/mo/performer")
        :valueType :db.type/string
        :cardinality :db.cardinality/one}
   #:db{:ident (keyword "http://purl.org/ontology/mo/producer")
        :valueType :db.type/string
        :cardinality :db.cardinality/one}
   #:db{:ident (keyword "http://purl.org/ontology/mo/record_number")
        :valueType :db.type/long
        :cardinality :db.cardinality/one}
   #:db{:ident (keyword "http://purl.org/ontology/mo/release")
        :valueType :db.type/string
        :cardinality :db.cardinality/one}
   #:db{:ident (keyword "http://purl.org/stuff/rev#hasReview")
        :valueType :db.type/ref
        :cardinality :db.cardinality/many}
   #:db{:ident (keyword "http://purl.org/stuff/rev#rating")
        :valueType :db.type/long
        :cardinality :db.cardinality/one}
   #:db{:ident (keyword "http://purl.org/stuff/rev#reviewer")
        :valueType :db.type/ref
        :cardinality :db.cardinality/one}
   #:db{:ident (keyword "http://purl.org/stuff/rev#text")
        :valueType :db.type/string
        :cardinality :db.cardinality/one}
   #:db{:ident (keyword "http://purl.org/stuff/rev#title")
        :valueType :db.type/string
        :cardinality :db.cardinality/one}
   #:db{:ident (keyword "http://purl.org/stuff/rev#totalVotes")
        :valueType :db.type/long
        :cardinality :db.cardinality/one}
   #:db{:ident (keyword "http://schema.org/actor")
        :valueType :db.type/ref
        :cardinality :db.cardinality/many}
   #:db{:ident (keyword "http://schema.org/aggregateRating")
        :valueType :db.type/long
        :cardinality :db.cardinality/one}
   #:db{:ident (keyword "http://schema.org/author")
        :valueType :db.type/ref
        :cardinality :db.cardinality/many}
   #:db{:ident (keyword "http://schema.org/award")
        :valueType :db.type/string
        :cardinality :db.cardinality/many}
   #:db{:ident (keyword "http://schema.org/birthDate")
        :valueType :db.type/string
        :cardinality :db.cardinality/one}
   #:db{:ident (keyword "http://schema.org/bookEdition")
        :valueType :db.type/long
        :cardinality :db.cardinality/one}
   #:db{:ident (keyword "http://schema.org/caption")
        :valueType :db.type/string
        :cardinality :db.cardinality/one}
   #:db{:ident (keyword "http://schema.org/contactPoint")
        :valueType :db.type/ref
        :cardinality :db.cardinality/one}
   #:db{:ident (keyword "http://schema.org/contentRating")
        :valueType :db.type/long
        :cardinality :db.cardinality/one}
   #:db{:ident (keyword "http://schema.org/contentSize")
        :valueType :db.type/long
        :cardinality :db.cardinality/one}
   #:db{:ident (keyword "http://schema.org/datePublished")
        :valueType :db.type/string
        :cardinality :db.cardinality/one}
   #:db{:ident (keyword "http://schema.org/description")
        :valueType :db.type/string
        :cardinality :db.cardinality/one}
   #:db{:ident (keyword "http://schema.org/director")
        :valueType :db.type/ref
        :cardinality :db.cardinality/one}
   #:db{:valueType :db.type/long
        :cardinality :db.cardinality/one
        :ident (keyword "http://schema.org/duration")}
   #:db{:ident (keyword "http://schema.org/editor")
        :valueType :db.type/ref
        :cardinality :db.cardinality/many}
   #:db{:ident (keyword "http://schema.org/eligibleQuantity")
        :valueType :db.type/long
        :cardinality :db.cardinality/one}
   #:db{:ident (keyword "http://schema.org/eligibleRegion")
        :valueType :db.type/ref
        :cardinality :db.cardinality/many}
   #:db{:ident (keyword "http://schema.org/email")
        :valueType :db.type/string
        :cardinality :db.cardinality/one}
   #:db{:ident (keyword "http://schema.org/employee")
        :valueType :db.type/ref
        :cardinality :db.cardinality/many}
   #:db{:ident (keyword "http://schema.org/expires")
        :valueType :db.type/string
        :cardinality :db.cardinality/one}
   #:db{:ident (keyword "http://schema.org/faxNumber")
        :valueType :db.type/long
        :cardinality :db.cardinality/one}
   #:db{:ident (keyword "http://schema.org/isbn")
        :valueType :db.type/long
        :cardinality :db.cardinality/one}
   #:db{:ident (keyword "http://schema.org/jobTitle")
        :valueType :db.type/string
        :cardinality :db.cardinality/one}
   #:db{:ident (keyword "http://schema.org/keywords")
        :valueType :db.type/string
        :cardinality :db.cardinality/one}
   #:db{:ident (keyword "http://schema.org/language")
        :valueType :db.type/ref
        :cardinality :db.cardinality/many}
   #:db{:ident (keyword "http://schema.org/legalName")
        :valueType :db.type/string
        :cardinality :db.cardinality/one}
   #:db{:ident (keyword "http://schema.org/nationality")
        :valueType :db.type/ref
        :cardinality :db.cardinality/one}
   #:db{:ident (keyword "http://schema.org/numberOfPages")
        :valueType :db.type/long
        :cardinality :db.cardinality/one}
   #:db{:ident (keyword "http://schema.org/openingHours")
        :valueType :db.type/long
        :cardinality :db.cardinality/one}
   #:db{:ident (keyword "http://schema.org/paymentAccepted")
        :valueType :db.type/string
        :cardinality :db.cardinality/one}
   #:db{:ident (keyword "http://schema.org/priceValidUntil")
        :valueType :db.type/string
        :cardinality :db.cardinality/one}
   #:db{:ident (keyword "http://schema.org/printColumn")
        :valueType :db.type/long
        :cardinality :db.cardinality/one}
   #:db{:ident (keyword "http://schema.org/printEdition")
        :valueType :db.type/long
        :cardinality :db.cardinality/one}
   #:db{:ident (keyword "http://schema.org/printPage")
        :valueType :db.type/long
        :cardinality :db.cardinality/one}
   #:db{:ident (keyword "http://schema.org/printSection")
        :valueType :db.type/long
        :cardinality :db.cardinality/one}
   #:db{:ident (keyword "http://schema.org/producer")
        :valueType :db.type/string
        :cardinality :db.cardinality/one}
   #:db{:ident (keyword "http://schema.org/publisher")
        :valueType :db.type/string
        :cardinality :db.cardinality/one}
   #:db{:ident (keyword "http://schema.org/telephone")
        :valueType :db.type/long
        :cardinality :db.cardinality/one}
   #:db{:ident (keyword "http://schema.org/text")
        :valueType :db.type/string
        :cardinality :db.cardinality/one}
   #:db{:ident (keyword "http://schema.org/trailer")
        :valueType :db.type/ref
        :cardinality :db.cardinality/many}
   #:db{:ident (keyword "http://schema.org/url")
        :valueType :db.type/string
        :cardinality :db.cardinality/one}
   #:db{:ident (keyword "http://schema.org/wordCount")
        :valueType :db.type/long
        :cardinality :db.cardinality/one}
   #:db{:ident (keyword "http://www.geonames.org/ontology#parentCountry")
        :valueType :db.type/ref
        :cardinality :db.cardinality/one}
   #:db{:ident (keyword "http://www.w3.org/1999/02/22-rdf-syntax-ns#type")
        :valueType :db.type/ref
        :cardinality :db.cardinality/many}
   #:db{:ident (keyword "http://xmlns.com/foaf/age")
        :valueType :db.type/ref
        :cardinality :db.cardinality/one}
   #:db{:valueType :db.type/string
        :cardinality :db.cardinality/one
        :ident (keyword "http://xmlns.com/foaf/familyName")}
   #:db{:ident (keyword "http://xmlns.com/foaf/givenName")
        :valueType :db.type/string
        :cardinality :db.cardinality/one}
   #:db{:ident (keyword "http://xmlns.com/foaf/homepage")
        :valueType :db.type/ref
        :cardinality :db.cardinality/one}
   #:db{:ident :watdiv/ingest-time
        :valueType :db.type/long
        :cardinality :db.cardinality/one}
   #:db{:ident :watdiv/ingest-state
        :valueType :db.type/keyword
        :cardinality :db.cardinality/one
        :unique :db.unique/identity
        :index true}])
