Advantages of Algebraic Design
=================================

When you design the algebra of a library first, representations for 
the data types of the algebra doesn't matter as much. As long as they
support the required laws and functions, you do not even need to make
your own representations public. This makes it easy for primitive 
combinators to use cheap tricks internally that might otherwise break 
referential transparency.

There is a powerful idea here, namely, that a type is given meaning
based on its relationship to other types (Which are specified by the set
of functions and their laws), rather than its internal representations.

This is a view point often associated with category theory; it might be 
associated with OOD, although OO has not traditionally placed much 
emphasis on algebraic laws. Furthermore, a big reason for encapsulation in
OO is that objects often have some mutable state and making this public
would allow client code to violate constraints, a concern that is not as 
relevant in FP.

