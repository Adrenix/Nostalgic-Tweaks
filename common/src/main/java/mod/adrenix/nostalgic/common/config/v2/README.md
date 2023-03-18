# Temporary Package

This package contains new code that is intended to replace the current config system in the mod. The goal of this new
system is to:

- 1.) Make defining new tweaks easier.
- 2.) Simplify the config GUI structure.
- 3.) Simplify the retrieval process of tweak values.
- 4.) Simplify the network tweak processing pipeline.
- 5.) Significantly reduce the size of the client config file.
- 6.) Make config maintenance easier.
- 7.) Make implementing a preset system easier.

The GUI will be simplified by removing the current system of (Group > Category > Subcategory > Embedded) structure into
just two types: `TweakCategory` and `TweakGroup`. A tweak category is considered the "trunk" for a tree that holds a
collection of tweaks and tweak groups. A tweak group is a collection of tweaks. A tweak group can subscribe to a tweak
category or a different tweak group. The new GUI system will determine how to position elements based on subscriptions
which are defined when each tweak is built.

Once the current config system is redone, then the new preset system can begin development.