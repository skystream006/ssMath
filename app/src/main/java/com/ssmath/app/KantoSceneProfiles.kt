package com.ssmath.app

internal enum class KantoHabitat {
    MEADOW,
    RAINFOREST,
    VOLCANO,
    COAST,
    POND,
    RIVER,
    REEF,
    SEABED,
    ICE,
    FOREST,
    CANOPY,
    HIVE,
    SKY,
    MOUNTAIN,
    GRASSLAND,
    DESERT,
    BURROW,
    MOONLIT,
    CAVE,
    CITY,
    DOJO,
    POWER_PLANT,
    SWAMP,
    HAUNTED,
    RUINS,
    DIGITAL,
}

internal enum class KantoMotion {
    SWAY,
    CRAWL,
    HANG,
    FLUTTER,
    FLY,
    SCURRY,
    SLITHER,
    HOP,
    PROWL,
    GALLOP,
    SWIM,
    FLOAT,
    BOB,
    DIG,
    ROLL,
    PULSE,
    LEVITATE,
    TELEPORT,
    SPAR,
    STOMP,
    SPIN,
    BREATHE,
}

internal enum class KantoEffect {
    LEAVES,
    PETALS,
    FIRE,
    WATER_JET,
    BUBBLES,
    SILK,
    POLLEN,
    WIND,
    DUST,
    ELECTRIC,
    STARS,
    SPORES,
    RIPPLES,
    COINS,
    PSYCHIC,
    IMPACT,
    VINES,
    ICE,
    SLUDGE,
    MIST,
    ROCKS,
    HEARTS,
    MUSIC,
    SLASH,
    PIXELS,
    SLEEP,
}

internal data class KantoSceneProfile(
    val habitat: KantoHabitat,
    val motion: KantoMotion,
    val effect: KantoEffect,
    val action: String,
)

internal val kantoSceneProfiles: Map<Int, KantoSceneProfile> = mapOf(
    2 to KantoSceneProfile(
        KantoHabitat.MEADOW, KantoMotion.SWAY, KantoEffect.VINES,
        "swaying among meadow flowers and unfurling climbing vines",
    ),
    3 to KantoSceneProfile(
        KantoHabitat.RAINFOREST, KantoMotion.BREATHE, KantoEffect.PETALS,
        "breathing beneath rainforest trees as giant flower petals drift",
    ),
    5 to KantoSceneProfile(
        KantoHabitat.VOLCANO, KantoMotion.PROWL, KantoEffect.FIRE,
        "prowling a volcanic ridge and scattering tail-flame embers",
    ),
    6 to KantoSceneProfile(
        KantoHabitat.VOLCANO, KantoMotion.FLY, KantoEffect.FIRE,
        "flying above a glowing crater and breathing ribbons of fire",
    ),
    8 to KantoSceneProfile(
        KantoHabitat.POND, KantoMotion.SWIM, KantoEffect.BUBBLES,
        "swimming across a sheltered pond and blowing playful bubbles",
    ),
    9 to KantoSceneProfile(
        KantoHabitat.COAST, KantoMotion.STOMP, KantoEffect.WATER_JET,
        "stomping along the shore and firing twin jets of water",
    ),
    10 to KantoSceneProfile(
        KantoHabitat.FOREST, KantoMotion.CRAWL, KantoEffect.SILK,
        "crawling over forest leaves and trailing a thread of silk",
    ),
    11 to KantoSceneProfile(
        KantoHabitat.CANOPY, KantoMotion.HANG, KantoEffect.SILK,
        "hanging beneath canopy branches in a cradle of silk",
    ),
    12 to KantoSceneProfile(
        KantoHabitat.MEADOW, KantoMotion.FLUTTER, KantoEffect.POLLEN,
        "fluttering over meadow blossoms and scattering golden pollen",
    ),
    13 to KantoSceneProfile(
        KantoHabitat.FOREST, KantoMotion.CRAWL, KantoEffect.LEAVES,
        "crawling through forest litter and nudging fallen leaves",
    ),
    14 to KantoSceneProfile(
        KantoHabitat.HIVE, KantoMotion.HANG, KantoEffect.SILK,
        "hanging from a hive branch on fine cocoon threads",
    ),
    15 to KantoSceneProfile(
        KantoHabitat.HIVE, KantoMotion.FLY, KantoEffect.SLASH,
        "flying around a woodland hive and slashing with sharp stingers",
    ),
    16 to KantoSceneProfile(
        KantoHabitat.MEADOW, KantoMotion.HOP, KantoEffect.WIND,
        "hopping through meadow grass and stirring little gusts",
    ),
    17 to KantoSceneProfile(
        KantoHabitat.FOREST, KantoMotion.FLY, KantoEffect.WIND,
        "flying between forest trunks and sweeping the woodland with gusts",
    ),
    18 to KantoSceneProfile(
        KantoHabitat.SKY, KantoMotion.FLY, KantoEffect.WIND,
        "flying high above the clouds and carving powerful wind trails",
    ),
    19 to KantoSceneProfile(
        KantoHabitat.GRASSLAND, KantoMotion.SCURRY, KantoEffect.DUST,
        "scurrying through tall grass and kicking up tiny dust clouds",
    ),
    20 to KantoSceneProfile(
        KantoHabitat.RIVER, KantoMotion.SCURRY, KantoEffect.SLASH,
        "scurrying across a riverbank with quick slashing bites",
    ),
    21 to KantoSceneProfile(
        KantoHabitat.GRASSLAND, KantoMotion.HOP, KantoEffect.WIND,
        "hopping across open grassland and ruffling the grass with gusts",
    ),
    22 to KantoSceneProfile(
        KantoHabitat.SKY, KantoMotion.FLY, KantoEffect.WIND,
        "flying through open skies and leaving long curling wind wakes",
    ),
    23 to KantoSceneProfile(
        KantoHabitat.GRASSLAND, KantoMotion.SLITHER, KantoEffect.LEAVES,
        "slithering through grassy cover and parting low leaves",
    ),
    24 to KantoSceneProfile(
        KantoHabitat.SWAMP, KantoMotion.SLITHER, KantoEffect.SLUDGE,
        "slithering through marsh reeds and splashing poisonous sludge",
    ),
    26 to KantoSceneProfile(
        KantoHabitat.POWER_PLANT, KantoMotion.SCURRY, KantoEffect.ELECTRIC,
        "scurrying past power plant coils and discharging cheek sparks",
    ),
    27 to KantoSceneProfile(
        KantoHabitat.DESERT, KantoMotion.DIG, KantoEffect.DUST,
        "digging into a desert dune and tossing up soft sand",
    ),
    28 to KantoSceneProfile(
        KantoHabitat.DESERT, KantoMotion.ROLL, KantoEffect.SLASH,
        "rolling across desert dunes with sweeping slashes from its spines",
    ),
    29 to KantoSceneProfile(
        KantoHabitat.MEADOW, KantoMotion.HOP, KantoEffect.LEAVES,
        "hopping between meadow clumps and rustling broad leaves",
    ),
    30 to KantoSceneProfile(
        KantoHabitat.GRASSLAND, KantoMotion.PROWL, KantoEffect.DUST,
        "prowling through dry grass and raising a cautious trail of dust",
    ),
    31 to KantoSceneProfile(
        KantoHabitat.MOUNTAIN, KantoMotion.STOMP, KantoEffect.ROCKS,
        "stomping around a mountain nest and scattering protective stones",
    ),
    32 to KantoSceneProfile(
        KantoHabitat.GRASSLAND, KantoMotion.HOP, KantoEffect.SLASH,
        "hopping through tall grass and slashing with its small horn",
    ),
    33 to KantoSceneProfile(
        KantoHabitat.GRASSLAND, KantoMotion.PROWL, KantoEffect.SLASH,
        "prowling a grassy hillside and cutting sharp arcs with its horn",
    ),
    34 to KantoSceneProfile(
        KantoHabitat.MOUNTAIN, KantoMotion.STOMP, KantoEffect.IMPACT,
        "stomping across a mountain plateau with ground-shaking impacts",
    ),
    35 to KantoSceneProfile(
        KantoHabitat.MOONLIT, KantoMotion.HOP, KantoEffect.STARS,
        "hopping beneath the full moon amid twinkling star-shaped lights",
    ),
    36 to KantoSceneProfile(
        KantoHabitat.MOONLIT, KantoMotion.SWAY, KantoEffect.STARS,
        "swaying in a moonlit clearing as soft stars shimmer around it",
    ),
    37 to KantoSceneProfile(
        KantoHabitat.FOREST, KantoMotion.PROWL, KantoEffect.FIRE,
        "prowling a woodland path and breathing small foxfire flames",
    ),
    38 to KantoSceneProfile(
        KantoHabitat.RUINS, KantoMotion.SWAY, KantoEffect.FIRE,
        "swaying beside mossy ruins and curling wisps of ancient foxfire",
    ),
    40 to KantoSceneProfile(
        KantoHabitat.MEADOW, KantoMotion.BOB, KantoEffect.MUSIC,
        "bobbing among meadow flowers and singing a rippling melody",
    ),
    41 to KantoSceneProfile(
        KantoHabitat.CAVE, KantoMotion.FLY, KantoEffect.WIND,
        "flying through a dark cavern and stirring narrow drafts",
    ),
    42 to KantoSceneProfile(
        KantoHabitat.CAVE, KantoMotion.FLY, KantoEffect.WIND,
        "flying beneath cavern arches and driving broad gusts through the dark",
    ),
    43 to KantoSceneProfile(
        KantoHabitat.MEADOW, KantoMotion.SWAY, KantoEffect.LEAVES,
        "swaying in meadow soil and shaking its leafy crown",
    ),
    44 to KantoSceneProfile(
        KantoHabitat.RAINFOREST, KantoMotion.SWAY, KantoEffect.SPORES,
        "swaying in damp rainforest shade and releasing drifting spores",
    ),
    45 to KantoSceneProfile(
        KantoHabitat.RAINFOREST, KantoMotion.SPIN, KantoEffect.POLLEN,
        "spinning beneath rainforest ferns and spreading heavy flower pollen",
    ),
    46 to KantoSceneProfile(
        KantoHabitat.FOREST, KantoMotion.CRAWL, KantoEffect.SPORES,
        "crawling over a forest log and shedding tiny mushroom spores",
    ),
    47 to KantoSceneProfile(
        KantoHabitat.SWAMP, KantoMotion.CRAWL, KantoEffect.SPORES,
        "crawling through marsh shade under a cloud of mushroom spores",
    ),
    48 to KantoSceneProfile(
        KantoHabitat.FOREST, KantoMotion.HOP, KantoEffect.POLLEN,
        "hopping among forest flowers and shaking loose clinging pollen",
    ),
    49 to KantoSceneProfile(
        KantoHabitat.CANOPY, KantoMotion.FLUTTER, KantoEffect.POLLEN,
        "fluttering beneath treetops and shedding fine wing powder",
    ),
    50 to KantoSceneProfile(
        KantoHabitat.BURROW, KantoMotion.DIG, KantoEffect.DUST,
        "digging through a narrow burrow and puffing soil into the air",
    ),
    51 to KantoSceneProfile(
        KantoHabitat.BURROW, KantoMotion.BOB, KantoEffect.DUST,
        "bobbing above connected burrows and scattering loose soil",
    ),
    52 to KantoSceneProfile(
        KantoHabitat.CITY, KantoMotion.PROWL, KantoEffect.COINS,
        "prowling a city alley and batting shiny coins",
    ),
    53 to KantoSceneProfile(
        KantoHabitat.CITY, KantoMotion.PROWL, KantoEffect.SLASH,
        "prowling past city walls and tracing swift claw slashes",
    ),
    54 to KantoSceneProfile(
        KantoHabitat.POND, KantoMotion.BOB, KantoEffect.RIPPLES,
        "bobbing at a pond edge and making widening ripples",
    ),
    55 to KantoSceneProfile(
        KantoHabitat.RIVER, KantoMotion.SWIM, KantoEffect.WATER_JET,
        "swimming against a river current and sending water jets downstream",
    ),
    56 to KantoSceneProfile(
        KantoHabitat.CANOPY, KantoMotion.HOP, KantoEffect.LEAVES,
        "hopping between canopy branches and scattering fresh leaves",
    ),
    57 to KantoSceneProfile(
        KantoHabitat.MOUNTAIN, KantoMotion.SPAR, KantoEffect.IMPACT,
        "sparring on a rocky summit with bursts of furious impact",
    ),
    58 to KantoSceneProfile(
        KantoHabitat.CITY, KantoMotion.PROWL, KantoEffect.FIRE,
        "prowling a city street on patrol and breathing warning embers",
    ),
    59 to KantoSceneProfile(
        KantoHabitat.GRASSLAND, KantoMotion.GALLOP, KantoEffect.FIRE,
        "galloping across open grassland with blazing fire at its heels",
    ),
    60 to KantoSceneProfile(
        KantoHabitat.POND, KantoMotion.SWIM, KantoEffect.RIPPLES,
        "swimming around pond lilies and tracing little circular ripples",
    ),
    61 to KantoSceneProfile(
        KantoHabitat.POND, KantoMotion.BOB, KantoEffect.BUBBLES,
        "bobbing beside pond reeds and blowing bubbles over its belly spiral",
    ),
    62 to KantoSceneProfile(
        KantoHabitat.RIVER, KantoMotion.SPAR, KantoEffect.WATER_JET,
        "sparring in a rushing river and driving sprays of water with each punch",
    ),
    63 to KantoSceneProfile(
        KantoHabitat.RUINS, KantoMotion.TELEPORT, KantoEffect.PSYCHIC,
        "teleporting between weathered ruins in brief psychic flashes",
    ),
    64 to KantoSceneProfile(
        KantoHabitat.RUINS, KantoMotion.LEVITATE, KantoEffect.PSYCHIC,
        "levitating above ruined steps and channeling psychic rings through its spoon",
    ),
    65 to KantoSceneProfile(
        KantoHabitat.RUINS, KantoMotion.LEVITATE, KantoEffect.PSYCHIC,
        "levitating over ancient stones and focusing psychic waves with twin spoons",
    ),
    66 to KantoSceneProfile(
        KantoHabitat.MOUNTAIN, KantoMotion.SPAR, KantoEffect.IMPACT,
        "sparring beside mountain boulders and landing small practice impacts",
    ),
    67 to KantoSceneProfile(
        KantoHabitat.DOJO, KantoMotion.SPAR, KantoEffect.IMPACT,
        "sparring across a dojo floor with disciplined bursts of impact",
    ),
    68 to KantoSceneProfile(
        KantoHabitat.DOJO, KantoMotion.SPAR, KantoEffect.IMPACT,
        "sparring in the dojo and unleashing a flurry of four-armed impacts",
    ),
    69 to KantoSceneProfile(
        KantoHabitat.RAINFOREST, KantoMotion.SWAY, KantoEffect.VINES,
        "swaying on rainforest soil and stretching thin grasping vines",
    ),
    70 to KantoSceneProfile(
        KantoHabitat.CANOPY, KantoMotion.HANG, KantoEffect.SLUDGE,
        "hanging beneath a canopy branch and dripping sticky acidic droplets",
    ),
    71 to KantoSceneProfile(
        KantoHabitat.RAINFOREST, KantoMotion.SWAY, KantoEffect.VINES,
        "swaying above rainforest undergrowth and lashing its long vine",
    ),
    72 to KantoSceneProfile(
        KantoHabitat.COAST, KantoMotion.FLOAT, KantoEffect.BUBBLES,
        "floating through coastal shallows with bubbles around its tentacles",
    ),
    73 to KantoSceneProfile(
        KantoHabitat.REEF, KantoMotion.FLOAT, KantoEffect.BUBBLES,
        "floating above a coral reef and trailing bubbles between long tentacles",
    ),
    76 to KantoSceneProfile(
        KantoHabitat.MOUNTAIN, KantoMotion.ROLL, KantoEffect.ROCKS,
        "rolling down a mountain slope and sending loose rocks tumbling",
    ),
    77 to KantoSceneProfile(
        KantoHabitat.GRASSLAND, KantoMotion.GALLOP, KantoEffect.FIRE,
        "galloping through sunlit grass with flickering flames around its hooves",
    ),
    78 to KantoSceneProfile(
        KantoHabitat.GRASSLAND, KantoMotion.GALLOP, KantoEffect.FIRE,
        "galloping across rolling plains as its fiery mane streams embers",
    ),
    79 to KantoSceneProfile(
        KantoHabitat.RIVER, KantoMotion.SWAY, KantoEffect.RIPPLES,
        "swaying on a riverbank and dipping its tail into rippling water",
    ),
    80 to KantoSceneProfile(
        KantoHabitat.COAST, KantoMotion.BREATHE, KantoEffect.BUBBLES,
        "breathing slowly by the coast as seafoam bubbles drift past its shell",
    ),
    81 to KantoSceneProfile(
        KantoHabitat.POWER_PLANT, KantoMotion.LEVITATE, KantoEffect.ELECTRIC,
        "levitating beside power plant wires and arcing electricity between magnets",
    ),
    82 to KantoSceneProfile(
        KantoHabitat.POWER_PLANT, KantoMotion.SPIN, KantoEffect.ELECTRIC,
        "spinning near a generator and linking its magnets with electric arcs",
    ),
    83 to KantoSceneProfile(
        KantoHabitat.POND, KantoMotion.HOP, KantoEffect.SLASH,
        "hopping beside pond reeds and sweeping its leek in sharp slashing arcs",
    ),
    84 to KantoSceneProfile(
        KantoHabitat.GRASSLAND, KantoMotion.SCURRY, KantoEffect.DUST,
        "scurrying across dry grassland and leaving a quick-footed trail of dust",
    ),
    85 to KantoSceneProfile(
        KantoHabitat.GRASSLAND, KantoMotion.SCURRY, KantoEffect.WIND,
        "scurrying across open plains and whipping up gusts with its speed",
    ),
    86 to KantoSceneProfile(
        KantoHabitat.ICE, KantoMotion.SWIM, KantoEffect.BUBBLES,
        "swimming beside floating ice and puffing bubbles from its muzzle",
    ),
    87 to KantoSceneProfile(
        KantoHabitat.ICE, KantoMotion.SWIM, KantoEffect.ICE,
        "swimming beneath polar ice and trailing glittering frost",
    ),
    88 to KantoSceneProfile(
        KantoHabitat.SWAMP, KantoMotion.CRAWL, KantoEffect.SLUDGE,
        "crawling through a murky swamp and leaving splashes of sticky sludge",
    ),
    89 to KantoSceneProfile(
        KantoHabitat.SWAMP, KantoMotion.PULSE, KantoEffect.SLUDGE,
        "pulsing in a polluted marsh and throwing off thick sludge droplets",
    ),
    90 to KantoSceneProfile(
        KantoHabitat.SEABED, KantoMotion.BOB, KantoEffect.BUBBLES,
        "bobbing above the sandy seabed and letting bubbles escape its shell",
    ),
    91 to KantoSceneProfile(
        KantoHabitat.REEF, KantoMotion.BOB, KantoEffect.ICE,
        "bobbing above a cold reef and scattering icy shards",
    ),
    92 to KantoSceneProfile(
        KantoHabitat.HAUNTED, KantoMotion.FLOAT, KantoEffect.MIST,
        "floating through a haunted chamber in a drifting veil of ghostly mist",
    ),
    93 to KantoSceneProfile(
        KantoHabitat.HAUNTED, KantoMotion.LEVITATE, KantoEffect.SLASH,
        "levitating through a haunted hall and tracing ghostly claw slashes",
    ),
    94 to KantoSceneProfile(
        KantoHabitat.HAUNTED, KantoMotion.PROWL, KantoEffect.MIST,
        "prowling among haunted shadows and trailing a low spectral mist",
    ),
    95 to KantoSceneProfile(
        KantoHabitat.CAVE, KantoMotion.SLITHER, KantoEffect.ROCKS,
        "slithering through a stone tunnel and dislodging cavern rocks",
    ),
    96 to KantoSceneProfile(
        KantoHabitat.MOONLIT, KantoMotion.SWAY, KantoEffect.SLEEP,
        "swaying beneath moonlit trees and releasing sleepy dream motes",
    ),
    97 to KantoSceneProfile(
        KantoHabitat.MOONLIT, KantoMotion.SWAY, KantoEffect.PSYCHIC,
        "swaying in a moonlit grove and casting hypnotic psychic rings",
    ),
    98 to KantoSceneProfile(
        KantoHabitat.COAST, KantoMotion.SCURRY, KantoEffect.BUBBLES,
        "scurrying sideways over coastal sand and blowing foamy bubbles",
    ),
    99 to KantoSceneProfile(
        KantoHabitat.COAST, KantoMotion.SCURRY, KantoEffect.WATER_JET,
        "scurrying along a tidal beach and blasting water past its giant claw",
    ),
    100 to KantoSceneProfile(
        KantoHabitat.POWER_PLANT, KantoMotion.ROLL, KantoEffect.ELECTRIC,
        "rolling across a power plant floor and shedding crackling sparks",
    ),
    101 to KantoSceneProfile(
        KantoHabitat.POWER_PLANT, KantoMotion.BOB, KantoEffect.ELECTRIC,
        "bobbing beside power plant machinery with building electric surges",
    ),
    102 to KantoSceneProfile(
        KantoHabitat.FOREST, KantoMotion.BOB, KantoEffect.PSYCHIC,
        "bobbing together on the forest floor and exchanging psychic pulses",
    ),
    103 to KantoSceneProfile(
        KantoHabitat.RAINFOREST, KantoMotion.SWAY, KantoEffect.LEAVES,
        "swaying like a rainforest palm and scattering long fronds",
    ),
    104 to KantoSceneProfile(
        KantoHabitat.MOUNTAIN, KantoMotion.HOP, KantoEffect.DUST,
        "hopping along a lonely mountain path and stirring bone-dry dust",
    ),
    105 to KantoSceneProfile(
        KantoHabitat.MOUNTAIN, KantoMotion.SPAR, KantoEffect.SLASH,
        "sparring on a rocky ledge and sweeping its bone through slashing arcs",
    ),
    106 to KantoSceneProfile(
        KantoHabitat.DOJO, KantoMotion.SPAR, KantoEffect.IMPACT,
        "sparring on dojo mats and striking with long-reaching kicking impacts",
    ),
    107 to KantoSceneProfile(
        KantoHabitat.DOJO, KantoMotion.SPAR, KantoEffect.IMPACT,
        "sparring inside the dojo and landing rapid boxing impacts",
    ),
    108 to KantoSceneProfile(
        KantoHabitat.MEADOW, KantoMotion.SWAY, KantoEffect.LEAVES,
        "swaying through meadow plants and sweeping leaves with its long tongue",
    ),
    109 to KantoSceneProfile(
        KantoHabitat.CITY, KantoMotion.FLOAT, KantoEffect.MIST,
        "floating above a city alley and venting clouds of gaseous mist",
    ),
    110 to KantoSceneProfile(
        KantoHabitat.CITY, KantoMotion.BOB, KantoEffect.MIST,
        "bobbing between city rooftops and exhaling twin clouds of smog",
    ),
    111 to KantoSceneProfile(
        KantoHabitat.MOUNTAIN, KantoMotion.STOMP, KantoEffect.ROCKS,
        "stomping over a rugged mountain trail and jolting rocks aside",
    ),
    112 to KantoSceneProfile(
        KantoHabitat.MOUNTAIN, KantoMotion.STOMP, KantoEffect.IMPACT,
        "stomping across a craggy ridge with heavy armored impacts",
    ),
    113 to KantoSceneProfile(
        KantoHabitat.MEADOW, KantoMotion.HOP, KantoEffect.HEARTS,
        "hopping through a peaceful meadow and sharing warm healing hearts",
    ),
    114 to KantoSceneProfile(
        KantoHabitat.FOREST, KantoMotion.SWAY, KantoEffect.VINES,
        "swaying in tangled forest undergrowth and looping its blue vines",
    ),
    115 to KantoSceneProfile(
        KantoHabitat.GRASSLAND, KantoMotion.HOP, KantoEffect.IMPACT,
        "hopping across grassy plains and landing with sturdy thudding impacts",
    ),
    116 to KantoSceneProfile(
        KantoHabitat.REEF, KantoMotion.SWIM, KantoEffect.BUBBLES,
        "swimming among reef corals and blowing bubbles through its snout",
    ),
    117 to KantoSceneProfile(
        KantoHabitat.REEF, KantoMotion.SWIM, KantoEffect.WATER_JET,
        "swimming past jagged coral and firing narrow jets from its snout",
    ),
    118 to KantoSceneProfile(
        KantoHabitat.POND, KantoMotion.SWIM, KantoEffect.RIPPLES,
        "swimming beneath pond blossoms and drawing delicate ripples with its fins",
    ),
    119 to KantoSceneProfile(
        KantoHabitat.RIVER, KantoMotion.SWIM, KantoEffect.WATER_JET,
        "swimming up a rocky river and spraying water around its horn",
    ),
    120 to KantoSceneProfile(
        KantoHabitat.COAST, KantoMotion.SPIN, KantoEffect.BUBBLES,
        "spinning in coastal shallows and circling its bright core with bubbles",
    ),
    121 to KantoSceneProfile(
        KantoHabitat.REEF, KantoMotion.SPIN, KantoEffect.PSYCHIC,
        "spinning above a luminous reef and radiating psychic rings from its gem",
    ),
    122 to KantoSceneProfile(
        KantoHabitat.CITY, KantoMotion.SWAY, KantoEffect.PSYCHIC,
        "swaying across a city plaza and forming rings of psychic light",
    ),
    123 to KantoSceneProfile(
        KantoHabitat.FOREST, KantoMotion.PROWL, KantoEffect.SLASH,
        "prowling through forest thickets and sweeping its scythes in sharp slashes",
    ),
    124 to KantoSceneProfile(
        KantoHabitat.ICE, KantoMotion.SWAY, KantoEffect.MUSIC,
        "swaying across an icy stage and singing a lilting tune",
    ),
    125 to KantoSceneProfile(
        KantoHabitat.POWER_PLANT, KantoMotion.STOMP, KantoEffect.ELECTRIC,
        "stomping near humming generators and crackling with stored electricity",
    ),
    126 to KantoSceneProfile(
        KantoHabitat.VOLCANO, KantoMotion.PROWL, KantoEffect.FIRE,
        "prowling beside molten lava and breathing bursts of searing flame",
    ),
    127 to KantoSceneProfile(
        KantoHabitat.FOREST, KantoMotion.SPAR, KantoEffect.SLASH,
        "sparring among forest roots and cutting the air with its pincers",
    ),
    128 to KantoSceneProfile(
        KantoHabitat.GRASSLAND, KantoMotion.GALLOP, KantoEffect.DUST,
        "galloping over a dry prairie and throwing up a charging dust cloud",
    ),
    129 to KantoSceneProfile(
        KantoHabitat.POND, KantoMotion.HOP, KantoEffect.RIPPLES,
        "hopping out of a shallow pond and landing in splashy ripples",
    ),
    130 to KantoSceneProfile(
        KantoHabitat.COAST, KantoMotion.SWIM, KantoEffect.WATER_JET,
        "swimming through coastal surf and unleashing powerful torrents of water",
    ),
    131 to KantoSceneProfile(
        KantoHabitat.ICE, KantoMotion.SWIM, KantoEffect.MUSIC,
        "swimming between ice floes and singing across the water",
    ),
    132 to KantoSceneProfile(
        KantoHabitat.SWAMP, KantoMotion.PULSE, KantoEffect.SLUDGE,
        "pulsing beside a marsh pool and flicking gelatinous droplets",
    ),
    133 to KantoSceneProfile(
        KantoHabitat.FOREST, KantoMotion.HOP, KantoEffect.LEAVES,
        "hopping along a woodland trail and tumbling through fallen leaves",
    ),
    134 to KantoSceneProfile(
        KantoHabitat.RIVER, KantoMotion.SWIM, KantoEffect.RIPPLES,
        "swimming through a clear river and leaving smooth rippling wakes",
    ),
    135 to KantoSceneProfile(
        KantoHabitat.GRASSLAND, KantoMotion.SCURRY, KantoEffect.ELECTRIC,
        "scurrying through grassland and shedding sparks from its pointed fur",
    ),
    136 to KantoSceneProfile(
        KantoHabitat.MEADOW, KantoMotion.PROWL, KantoEffect.FIRE,
        "prowling a sunny meadow and breathing warm flickering flames",
    ),
    137 to KantoSceneProfile(
        KantoHabitat.DIGITAL, KantoMotion.PULSE, KantoEffect.PIXELS,
        "pulsing through a digital grid while shedding square pixels",
    ),
    138 to KantoSceneProfile(
        KantoHabitat.SEABED, KantoMotion.CRAWL, KantoEffect.BUBBLES,
        "crawling over ancient seabed stones and trailing shell bubbles",
    ),
    139 to KantoSceneProfile(
        KantoHabitat.SEABED, KantoMotion.SWIM, KantoEffect.WATER_JET,
        "swimming above seabed fossils and propelling itself with water jets",
    ),
    140 to KantoSceneProfile(
        KantoHabitat.SEABED, KantoMotion.CRAWL, KantoEffect.BUBBLES,
        "crawling across the seabed and releasing tiny trapped bubbles",
    ),
    141 to KantoSceneProfile(
        KantoHabitat.COAST, KantoMotion.PROWL, KantoEffect.SLASH,
        "prowling an ancient shoreline and slashing with curved fossil scythes",
    ),
    142 to KantoSceneProfile(
        KantoHabitat.MOUNTAIN, KantoMotion.FLY, KantoEffect.WIND,
        "flying around mountain spires and riding fierce prehistoric gusts",
    ),
    143 to KantoSceneProfile(
        KantoHabitat.FOREST, KantoMotion.BREATHE, KantoEffect.SLEEP,
        "breathing in a forest clearing while sleepy symbols drift overhead",
    ),
    144 to KantoSceneProfile(
        KantoHabitat.ICE, KantoMotion.FLY, KantoEffect.ICE,
        "flying over a frozen landscape and scattering crystalline snowflakes",
    ),
    145 to KantoSceneProfile(
        KantoHabitat.SKY, KantoMotion.FLY, KantoEffect.ELECTRIC,
        "flying through stormy skies and releasing jagged lightning bolts",
    ),
    146 to KantoSceneProfile(
        KantoHabitat.VOLCANO, KantoMotion.FLY, KantoEffect.FIRE,
        "flying above volcanic peaks and trailing a legendary plume of flame",
    ),
    147 to KantoSceneProfile(
        KantoHabitat.RIVER, KantoMotion.SWIM, KantoEffect.RIPPLES,
        "swimming along a quiet river and leaving ripples behind its slender body",
    ),
    148 to KantoSceneProfile(
        KantoHabitat.RIVER, KantoMotion.FLOAT, KantoEffect.MIST,
        "floating above a hidden river and gathering weather-changing mist",
    ),
    149 to KantoSceneProfile(
        KantoHabitat.COAST, KantoMotion.FLY, KantoEffect.WIND,
        "flying along a rugged coast and sweeping sea breezes toward shore",
    ),
    150 to KantoSceneProfile(
        KantoHabitat.CAVE, KantoMotion.LEVITATE, KantoEffect.PSYCHIC,
        "levitating in a secluded cavern and radiating powerful psychic waves",
    ),
    151 to KantoSceneProfile(
        KantoHabitat.RAINFOREST, KantoMotion.TELEPORT, KantoEffect.PSYCHIC,
        "teleporting between rainforest clearings in soft psychic flashes",
    ),
)

internal fun kantoSceneProfile(ndex: Int): KantoSceneProfile =
    requireNotNull(kantoSceneProfiles[ndex]) { "No Kanto scene for Ndex $ndex" }
