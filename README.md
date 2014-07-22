testifj
=======

Testing for Java 8

The testifj framework provides expressive testing for Java 8 based largely on lambdas and DSLs. The framework
allows testing syntax such as "expect(X).toBe(Y)" etc. Typically, feedback given from a failure of such an expression
would be unexpressive, unless the individual matchers are descriptive. However, one of the key features of testifj
is that matchers are easy to read and the framework deals with expressive descriptions. The framework accomplishes
this by reverse engineering the code that causes the execution failure and thus expressing the failure in details.

Basic example corresponding to assertEquals(a, b) and corresponding failure message:

    expect(1).toBe(2)

    java.lang.AssertionError: Expected 1 to be 2

Example using evaluated expressions in expectations and corresponding failure message:

    int n = 1;
    int m = 2;
    expect(n).toBe(m)

    java.lang.AssertionError: Expected n => 1 to be m => 2

Examples of using evaluated expressions and matchers, and the corresponding failure message:

    String getName() { return "foo"; }
    expect(getName()).toBe(stringThat(contains("foobar")))

    java.lang.AssertionError: Expected getName() => "foo" to be string that contains "foobar"


    expect(Arrays.asList("foo", "bar")).toBe(collectionThat(containsElement("baz")))

    java.lang.AssertionError: Expected Arrays.asList("foo", "bar") => ["foo", "bar"] to be collection that contains element "baz"

Example of using lambdas to perform verification, and the corresponding failure message:

    expect("foobar").toMatch(s -> s.length() = 10)

    java.lang.AssertionError: Expected "foobar" to match _.length() = 10
