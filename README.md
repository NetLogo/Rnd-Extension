# NetLogo Rnd Extension

This extension adds the `rnd:weighted-n-of` primitive to NetLogo.

It's currently a very rough, mostly untested, work in progress. Don't use in production.

## Usage

#### `rnd:weighted-n-of` _size_ _agentset_ _reporter-task_
#### `rnd:weighted-n-of` _size_ _list_ _reporter-task_

Picks _size_ items from _agentset_ or _list_, with the probability of each item picked proportional to the weight reported by _reporter-task_ for this item.

## Building

Run `./sbt package` to build the extension.

If the build succeeds, `rnd.jar` will be created.

## Credits

Authored by Nicolas Payette.

## Terms of Use

[![CC0](http://i.creativecommons.org/p/zero/1.0/88x31.png)](http://creativecommons.org/publicdomain/zero/1.0/)

The NetLogo Rnd extension is in the public domain. To the extent possible under law, Uri Wilensky has waived all copyright and related or neighboring rights.
