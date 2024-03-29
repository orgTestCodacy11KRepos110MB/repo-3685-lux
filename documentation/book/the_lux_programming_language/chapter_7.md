# Chapter 7: Polymorphism a.k.a. interfaces and implementations

_Where types and values collide._

---

You endured all that tedious talk about types; but it wasn't for nothing.

Now, you'll see types in action, as they take a new _shape_... and a new _purpose_.

Many programming languages have some kind of polymorphism system or module system.

You know what I'm talking about.

Object-oriented languages have classes with methods that can be overriden by their subclasses. The moment you call one of those methods on an object, the run-time system selects for you the correct implementation, based on the class hierarchy.

Or maybe you come from Haskell, where they have type-classes, that basically perform the same process, but during compilation. Types are checked, instances get picked, and the proper functions and constants get plugged-in.

Or maybe, you come from the world of ML (specially Standard ML), where they have a _module system_ based on signatures and structures. In those systems, the function implementations you want don't get selected for you automatically (you have to pick them yourself), but you tend to have more control when it comes to choosing what to use.

The origin of Lux's polymorphism system is I... um... _borrowed_ it from the SML guys.

I re-named signatures as _interfaces_ and structures as _implementations_ to give them more recognizable names.

But I also added my own little twist.

You see, polymorphism/module systems in programming languages tend to live in a mysterious world that is removed from the rest of the language.

It's a similar situation as with types.

Remember Lux's type system?

Most languages keep their types separate from their values.

Types are just some cute annotations you put in your code to keep the compiler happy.

Lux's types, on the other hand, are alive; for they are values.

Nothing stops you from using them, transforming them and analyzing them in ways that go beyond the language designer's imagination (_that would be **me**_).

Well, there's a similar story to tell about polymorphism/module systems.

The run-time/compiler chooses everything for you; and even when you choose for yourself, you're still somewhat limited in what you can do.

Implementations are not values, and there is a fundamental division between them and the rest of the language.

But **not** in Lux.

Lux's polymorphism system is actually based on regular types and values.

And because types are values, that means it's just ~~turtles~~ values all the way down.

_But, how does it work?_

**Read on!**

## Interfaces

They provide a description of the functionality expected of proper implementations.

They have a list of expected member values/functions, with their associated types.

Here's an example:

```clojure
(type .public (Order a)
  (Interface
   (is (Equivalence a)
       equivalence)

   (is (-> a a Bit)
       <)))
```

That _interface_ definition comes from the `library/lux/abstract/order` module, and it deals with _ordered_ types; that is, types for which you can compare their values in ways that imply some sort of sequential order.

It's polymorphic/parameterized because this interface must be able to adapt to any type that fits its requirements.

Also, you may notice that it has a member called `equivalence`, of type `(Equivalence a)`.

The reason is that interfaces can expand upon (or be based on) other interfaces (such as `Equivalence`).

_How do interfaces differ from types?_

They don't.

They're actually implemented as _types_.

Specifically, as tuple/record types.

You see, if I can create a record type with one field for every expected definition in a interface, then that's all I need.

## Implementations

They are the other side of the coin.

If interfaces are record types, then that means implementations must be actual records.

Let's take a look at how you make one:

```clojure
(def .public order
  (Order Frac)
  (implementation
    (def equivalence ..equivalence)
    (def < ..<)))
```

This implementation comes from `library/lux/math/number/frac`.

As you may notice, implementations have names; unlike in object-oriented languages where the "implementation" would just be the implemented methods of a class, or Haskell where _instances_ are anonymous.

For implementations, the convention is just to name them as lower-cased versions of the interfaces they implement.

Here is another example, from the `library/lux/data/collection/list` module:

```clojure
(def .public monoid
  (All (_ a)
    (Monoid (List a)))
  (implementation
    (def identity
      {.#End})
    
    (def (compose xs ys)
      (when xs
        {.#End}        ys
        {.#Item x xs'} {.#Item x (compose xs' ys)}))))
```

The reason why implementations have names (besides the fact that they are definitions like any other), is that you can _usually_ construct multiple valid implementations for the same combination of interfaces and parameter types.

That would require you to distinguish each implementation in some way in order to use it.

This is one cool advantage over Haskell's _type-classes_ and _instances_, where you can only have one instance for any combination of type-class and parameter.

	Haskellers often resort to _"hacks"_ such as using newtype to try to get around this limitation.

The upside of having the run-time/compiler pick the implementation for you is that you can avoid some boilerplate when writing polymorphic code.

The upside of picking the implementation yourself is that you get more control and predictability over what's happening (which is specially useful when you consider that _implementations_ are first-class values).

What's the big importance of _implementations_ being first-class values?

Simple: it means you can create your own _implementations_ at run-time based on arbitrary data and logic, and you can combine and transform _implementations_ however you want.

Standard ML offers something like that by a mechanism they call "functors" (unrelated to a concept of "functor" we'll see in a later chapter), but they are more like _magical functions_ that the compiler uses to combine _structures_ in limited ways.

In Lux, we dispense with the formalities and just use regular old functions and values to get the job done.

## How to use implementations

We've put functions and values inside our implementations.

It's time to get them out and use them.

There are 2 main ways to use the stuff inside your implementations: `use` and `at`.

Let's check them out.

```clojure
... Opens an implementation and generates a definition for each of its members (including nested members).

... For example:
(use "i::[0]" library/lux/math/number/int.order)

... Will generate:
(def .private i::= (at library/lux/math/number/int.order =))
(def .private i::< (at library/lux/math/number/int.order <))
```

The `use` macro serves as a directive that creates private/un-exported definitions in your module for every member of a particular implementation.

You may also give it an optional _aliasing pattern_ for the definitions, in case you want to avoid any name clash.

	You might want to check out [Appendix C](appendix_c.md) to discover a pattern-matching macro version of `use` called `^open`.

```clojure
... Allows accessing the value of a implementation's member.
(is (-> Int Text)
    (at library/lux/math/number/int.decimal encoded))

... Also allows using that value as a function.
(at library/lux/math/number/int.decimal encoded +123)

... => "+123"
```

`at` is for when you want to use individual parts of a implementation immediately in your code, instead of opening them first.

	Psss! Did you notice `at` is _piping compatible_?

Also, you don't really need to worry about boilerplate related to using implementations.

There is a module called `library/lux/type/implicit` which gives you a macro called `a/an` for using implementations without actually specifying which one you need.

	Psss! This macro also has 2 shorter aliases: `a` and `an`.

The macro infers everything for you based on the types of the arguments, the expected type of the expression, and the implementations available in the environment.

For more information about that, head over to [Appendix F](appendix_f.md) to read more about that.

## Implementations as values

I can't emphasize enough that _implementations_ are values.

And to exemplify it for you, here's a function from the `library/lux/abstract/monad` module that takes in an implementation (among other things) and uses it within its code:

```clojure
(def .public (each monad f xs)
  (All (_ M a b)
    (-> (Monad M) (-> a (M b)) (List a) (M (List b))))
  (when xs
    {.#End}
    (at monad in {.#End})

    {.#Item x xs'}
    (do monad
      [y (f x)
       ys (each monad f xs')]
      (in {.#Item y ys}))))
```

`Monad` is an interface and the `each` function takes arbitrary `Monad` implementations and can work with any of them without any issue.

---

_Interfaces_ and _implementation_ are the main mechanism for writing ad-hoc polymorphic code in Lux, and they allow flexible and precise control over polymorphism.

It may be the case that in the future Lux includes new mechanisms for achieving the same goals (I believe in having variety), but the spirit of implementing things in terms of accessible values anybody can manipulate will likely underlie every such mechanism.

Now that we've discussed _interfaces_ and _implementations_, it's time to talk about a _very special family of interfaces_.

See you in [the next chapter](chapter_8.md)!

