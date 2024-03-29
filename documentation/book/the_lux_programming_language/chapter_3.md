# Chapter 3: Syntax and data-types

_Where you will learn the what Lux code is made of._

---

## Syntax for data-types

* `Bit`s look like this:

```clojure
#0 ... false
#1 ... true
```

* `Nat`s look like this:

```clojure
0
123
0456
123,456,789
```

* `Int`s look like this:

```clojure
+0
-123
+0456
-123,456,789
```

* `Rev`s look like this:

```clojure
.123
.04,56
.7890
```

* `Frac`s look like this:

```clojure
+123.456
-456.7890
+0.001
123,456.789
```

* `Text`s look like this:

```clojure
"This is a single-line text"
```

* `Unit` looks like this:

```clojure
[]
```

* Variants look like this:

```clojure
{#Foo}
{#Bar 10 +20.0 "thirty"}
```

* Tuples look like this:

```clojure
[123 ["nested" #tuple] true]
```

* Records look like this:

```clojure
[#name "Lux" #paradigm {#Functional} #platforms (list {#JVM})]
```

**Note**: As you can see, commas (`,`) can be used as separators for the numeric literals.

---

Regarding _those label thingies_ we saw earlier, they're called **labels**, and the reason they're not mentioned as part of Lux's data-types is that they're not really data-types; they're just part of the language syntax.

They're used as part of the syntax for data-types, but they're not data-types in themselves.

Also, you can't just use anything you want as a label, as you first have to declare them.

We'll talk more about labels a bit later, when we talk about defining types.

---

Also, just from looking at the syntax for unit and tuples, you can see that they look quite similar. The reason is that unit is actually the empty tuple. I know it sounds odd, but for the most part you just have to think of unit as a kind of empty value, considering that it contains no information inside.

    It might sound specially odd that we have an "empty" value at all in the first place, but as it turns out, it's quite useful in a variety of situations.

    You're about to see one of those pretty soon.

In the section for variants, you can see 2 different alternatives, and you might wonder how do they differ.

Well, a variant is a pair of a _label_ (referred to as a _tag_) and a _single_ value.

That's right, I said **single** value; so you might be wondering how come we're associating 3 values with the `#Bar` tag.

It's pretty simple, actually: whenever you're trying to create a variant with more than one value, Lux just wraps all the values inside a tuple for you.

So, `{#Bar 10 +20.0 "thirty"}` is the same as `{#Bar [10 +20.0 "thirty"]}`.

Now, you might be thinking: _what's up with that `#Foo` variant?_

Well, sometimes you only care about a variant for its tag, and not for any value it may hold (for example, if you're trying to use a variant type as an enumeration).

In that case, you'll want to pair the tag with an empty value (since it has to be paired with something).

That's right! You've just witnessed **unit** value in action and you didn't even know it.

If you just write the tag in braces, with nothing else in there, Lux will stick a **unit** in there for you.

That means `{#Foo}` is the same as `{#Foo []}`.

---

You might have noticed that I mentioned **records** in this chapter, but not in the previous chapter, where I also talked about the basic data-types Lux offers.

The reason is that records are a bit of a magic trick in Lux.

That means records are not really a data-type that's distinct from the other ones.

In fact, records just offer you an alternative syntax for writing tuples.

That's right! `[#name "Lux" #paradigm {#Functional} #platforms (list {#JVM})]` could mean the same as `["Lux" {#Functional} (list {#JVM})]`, depending on the ordering imposed by the _slots_ (which is the name given to record _labels_).

---

Remember when I said that you needed to declare your labels?

Well, depending on the order in which you declare them, that means that `#name` could point to the first element in the tuple, or to another position entirely.

Also, in the same way that labels have a numeric value when it comes to their usage in tuples/records, that's also the case for variants.

For example, the `List` type has two tags: `.#End` and `.#Item`.

The `.#End` tag has value 0, while the `.#Item` tag has value 1.

That's what allows Lux to the able to identify which option it is working with at runtime when you're dealing with variants.

Labels belong to the module in which they were declared, and you must use the module name (or an alias) as a prefix when using tags.

That is why I've written `.#End` and `.#Item`, instead of `#End` and `#Item`.

However, you may forgo the prefixes if you're referring to tags which were defined in the same module in which they're being used.

## Types for data-types

_"But, wait!"_, you might say. _"We didn't talk about functions!"_

Patience, young grasshopper. We'll talk about those in the next chapter.

For now, let's talk about **types**.

The type-annotation macro is called `is`. You use it like this: `(is Some_Type some_value)`.

There is also a separate macro for type-coerciones that's called `as`, which is used the same way. However, you should probably steer clear off that one, unless you know what you're doing, since you can trick the compiler into thinking a value belongs to any type you want by using it.

Now that we know about type annotations, I'll show you some types by giving you some valid Lux expressions:

```clojure
(is Bit #1)
(is Bit .true)
(is Nat 123)
(is Int -123)
(is Rev .789)
(is Frac +456.789)
(is Text "YOLO")

(type Some_Enum
  (Variant
   {#Primitive}
   {#Variant}
   {#Tuple}))

(is [Int [Text Some_Enum] Bit]
    [10 ["nested" {#Tuple}] .false])

(type Quux
  (Variant
   {#Foo}
   {#Bar Int Frac Text}))

(is Quux {#Foo})

(is Quux {#Bar 10 +20.0 "thirty"})

(type Lang
  (Record
   [#name Text
    #paradigm Paradigm
    #platforms (List Platform)]))

(is Lang
    [#name "Lux"
     #paradigm {#Functional}
     #platforms (list {#JVM})])

(is Lang
    ["Lux" {#Functional} (list {#JVM})])

(is [Text Paradigm (List Platform)]
    [#name "Lux"
     #paradigm {#Functional}
     #platforms (list {#JVM})])
```

    By the way, the value of a type-annotation or a type-coearcion expression is just the value being annotated/coerced. So `(is Bit #1)` simply yields `#1`.

_What is that `type` thingie?_

It's just a macro for defining types. We'll learn more about it in a future chapter.

The labels that get mentioned in the type definition get automatically declared, and the order in which they appear determines their value.

`#Foo` came first, so its value is 0. `#Bar`, as you may guess, gets the value 1.

Also, you might be wondering what's the difference between `List` and `list`. Well, the first one is the type of lists (or a type-constructor for list types, however you want to look at it). The second one is a _macro_ for constructing actual list values. `List` can only take one argument (the type of the list elements), while `list` can take any number of arguments (the elements that make up the list value).

---

Again, we haven't mentioned functions. But the next chapter is about them.

See you in [the next chapter](chapter_4.md)!

