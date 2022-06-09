# GlasshouseTweaks: A not-so-simple hit system implementation plugin for Spigot Minecraft

This plugin is designed to implement a hit system through integrating a discord bot that runs alongside this bot 
(which can already be found [here](https://github.com/nickelulz/ghb-hitbot-pluginbridge).)

### The 'Hit' System

The hit system on this bot was programmed with a few ideas in mind. Firstly, there are two types of hits: a 'public' 
hit (Bounty) and a private hit (Contract.) Bounties are open for anybody to claim but incur higher cooldowns and higher 
prices, and Contracts are secretive wherein a hirer negotiates with a specific person prior to placing the hit.

### Cooldowns

There are 3 types of cooldowns: placing a hit, being a contractor for a hit, and being targetted by a hit. Each one 
has a different default time (placing is 2 hours, contracting is 2 hours, and targetting is 4 hours) but these values 
can be changed by reconfiguring the bot.

### Anarchy Day

Built into the plugin is a special state known as 'anarchy day'. It is defined by a day of the week in which the bot 
goes into a state where **no kills are illegal, regardless of the circumstances**. This can be turned on or off and 
specified in 'config.yml'.

## Commands

* register
* leaderboard
* playerinfo
* glasshousetweaks
* bounty
* contract
* removehit
* transfer
* illegalkills