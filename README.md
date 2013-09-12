# Schematica

Schematica is an open-source (CC BY-NC-SA 3.0) modification for [Minecraft](http://www.minecraft.net/). The original project can be found [here](https://github.com/Lunatrius/Schematica), which is where you should download it from if you are interested. This fork mainly serves as a reliable backup for my local source code. 

I have several desires for this mod, which I aim to realize. However, this is my first ever Minecraft mod development, so don't get your hopes up.

Here's my list of desires, in the order in which I will attempt to realize them.

1. Give it the typical fun Minecraft gameplay. Here I think it could be nice to have an in-game building-plan item that links to a schematic on the harddrive. Such a schematic could be placeable on a workbench to render the sprite. The orientation of the building would then be dependent on your orientation w.r.t. the workbench.

2. Make schematic saving possible from within survival/creative. Here I imagine something like crafting a partial-plan item on a workbench. The partial-plan could be crafted with paper (and perhaps some extra's like ink/feather?) and would 'simply' store the workbench location in the item metadata. Two partial-plans can be crafted together into a full building-plan. This way, one can create a bounding box with two workbenches and export the volume within.

3. See if we can make it compatible with SMP. My priorities there would be to maintain the minecraft spirit. Players would never deal with filestorage but could craft reusable building plans, as well as exchange/trade building-plan items. Admins could be given a set of commands to import building plans from disk (either server or client storage, whichever is easier to implement).

Concrete Milestones for now:

1. Create a building-plan item

    1. Be able to store and load metadata with the item (either a path string, or the actual schematic file)
    
    2. Place a schematic sprite by right clicking a workbench with the building-plan item (I may implement a copy of workbench to minimize potential conflicts)
    
    3. _Should have_: Let the player's angle w.r.t. the workbench dictate the orientation of the schematic
    
    4. _Could have_: Display the sprite while hovering and update when player re-orients himself (may be too demanding in terms of computational resources, but would be cool)   

Again, no promises, this is simply a specification of intent!

## Getting started

The project requires a parent [MinecraftForge](https://github.com/MinecraftForge/MinecraftForge) project to successfully compile.

## Building the jar file

To build a jar file, that can be installed alongside MinecraftForge, you'll have to create the file build.properties with the following properties (the default values may be changed to your liking):

```
dir.project=${basedir}
dir.workspace=${dir.project}/..
dir.mcp=${dir.workspace}/mcp
dir.release=${dir.project}/release
```
