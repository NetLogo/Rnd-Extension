# NetLogo Rnd Extension

This extension adds the `rnd:weighted-n-of` primitive to NetLogo.

It's currently a very rough, mostly untested, work in progress. Don't use in production.

## Usage

#### `rnd:weighted-n-of` _size_ _agentset_ _reporter-task_
#### `rnd:weighted-n-of` _size_ _list_ _reporter-task_

From an agentset, reports an agentset of size _size_ randomly chosen from the input set, with no repeats.

From a list, reports a list of size _size_ randomly chosen from the input set, with no repeats. The items in the result appear in the same order that they appeared in the input list. (If you want them in random order, use shuffle on the result.)

In both cases, the probability of each item being picked is proportional to the weight reported by _reporter-task_ for this item.

It is an error for _size_ to be greater than the size of the input.

The weights reported by _reporter-task_ must not be negative, and there must be at least as many candidates with a positive weight (i.e., >= 0) than the number of requested items (_size_).

#### `rnd:weighted-one-of`  _agentset_ _reporter-task_
#### `rnd:weighted-one-of`  _list_ _reporter-task_

From an agentset, reports a random agent. If the agentset is empty, reports [`nobody`](http://ccl.northwestern.edu/netlogo/docs/dictionary.html#nobody).

From a list, reports a random list item. It is an error for the list to be empty.

In both cases, the probability of each item being picked is proportional to the weight reported by _reporter-task_ for this item.

## Building

Run `./sbt package` to build the extension.

If the build succeeds, `rnd.jar` will be created.

## Credits

Authored by Nicolas Payette.

## Terms of Use

[![CC0](http://i.creativecommons.org/p/zero/1.0/88x31.png)](http://creativecommons.org/publicdomain/zero/1.0/)

The NetLogo Rnd extension is in the public domain. To the extent possible under law, Uri Wilensky has waived all copyright and related or neighboring rights.
