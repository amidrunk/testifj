testifj
=======

Testing for Java 8

The testifj framework provides expressive testing for Java 8 based largely on lambdas and DSLs. The framework
allows testing syntax such as "expect(X).toBe(Y)" etc. Typically, feedback given from a failure of such an expression
would be unexpressive, unless the individual matchers are descriptive. However, one of the key features of testifj
is that matchers are easy to read and the framework deals with expressive descriptions. The framework accomplishes
this by reverse engineering the code that causes the execution failure and thus expressing the failure in details.
Examples:

expect(1).toBe(2) => "Expected 1 to be 2"

int n = 1;
expect(n).toBe(2) => "Expected n => 1 to be 2"

String getName() { return "foo"; }
expect(getName()).toBe(stringThat(contains("foobar"))) => 'Expected getName() => "foo" to be string that contains "foobar"'

expect(Arrays.asList("foo", "bar")).toBe(collectionThat(containsElement("baz"))) => 'Expected Arrays.asList("foo", "bar") => ["foo", "bar"] to be collection that contains element "baz"'


expect("foobar").toMatch(s -> s.length() = 10) => 'Expected "foobar" to match _.length() = 10'