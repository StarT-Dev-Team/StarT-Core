package com.startechnology.start_core.lang;

import com.tterrag.registrate.providers.RegistrateLangProvider;

public class LangHandler {

    public static void init(RegistrateLangProvider provider) {
        provider.add("item.start_core.data_dna_disk.tooltip", "§7Storing Information in Life");
        provider.add("item.start_core.component_data_core.tooltip", "§7Data Beyond Transcendance");

        provider.add("item.start_core.bacteria_dormant.tooltip", "§7Reawaken this colony and discover its secrets");

        provider.add("gtceu.bacterial_breeding_vat", "Bacterial Breeding");

        provider.add("gtceu.bacterial_runic_mutator", "Bacterial Mutating");

        provider.add("gtceu.bacterial_hydrocarbon_harvester", "Bacterial Harvesting");

        provider.add("behaviour.start_core.bacteria.primary_output", "§5Primary output");
        provider.add("behaviour.start_core.bacteria.any_affinity", "§dCan be any one of the affinities");
        provider.add("behaviour.start_core.bacteria.maximum_shown_input", "§7Maximum amount consumed shown");
        provider.add("behaviour.start_core.bacteria.harvester_biomass_input", "§7Actual biomass consumption is §6100 * (2 ^ Metabolism)");
        provider.add("behaviour.start_core.bacteria.harvester_sugar_input", "§7Actual sugar consumption is §62 ^ Metabolism");
        provider.add("behaviour.start_core.bacteria.maximum_shown_output", "§7Maximum amount outputted shown");
        provider.add("behaviour.start_core.bacteria.harvester_primary_output", "§7Actual primary fluid output amount is §61500mB * Production");

        provider.add("behaviour.start_core.bacteria.secondary_output", "§5Secondary output");
        provider.add("behaviour.start_core.bacteria.harvester_secondary_output", "§7Actual secondary fluid output amount is §6750mB * Production");

        provider.add("behaviour.start_core.bacteria.tertiary_output", "§5Tertiary output");
        provider.add("behaviour.start_core.bacteria.harvester_tertiary_output", "§7Actual tertiary fluid output amount is §6250mB * Production");

        provider.add("behaviour.start_core.bacteria.super_output", "§5Super output");
        provider.add("behaviour.start_core.bacteria.harvester_super_output", "§7Actual super fluid output amount is §61000mB * Production");

        provider.add("behaviour.start_core.bacteria.bacteria_output", "§5Bacteria output");
        provider.add("behaviour.start_core.bacteria.harvester_bacteria_output", "§7Actual bacteria fluid output amount is §6500mB * Production");

        provider.add("behaviour.start_core.bacteria.no_stats", "§7The bacteria with any stats");
        provider.add("behaviour.start_core.bacteria.stats_header", "§fBacteria Stats:");
        provider.add("behaviour.start_core.bacteria.affinities_header", "§fAffinities:");
        provider.add("behaviour.start_core.bacteria.affinity_primary", "§7Primary: %1$s");
        provider.add("behaviour.start_core.bacteria.affinity_secondary", "§7Secondary: %1$s");
        provider.add("behaviour.start_core.bacteria.affinity_tertiary", "§7Tertiary: %1$s");

        provider.add("behaviour.start_core.bacteria.stat_production", "§7Production: {0}");
        provider.add("behaviour.start_core.bacteria.stat_metabolism", "§7Metabolism: {0}");
        provider.add("behaviour.start_core.bacteria.stat_mutability", "§7Mutability: {0}");

        provider.add("behaviour.start_core.bacteria.affinity_super", "§7Superfluid: %1$s");

        provider.add("behaviour.start_core.bacteria.stat_affinity", "§dHas affinity with %1$s");
        provider.add("behaviour.start_core.bacteria.possible_affinities", "§dColony Affinities: %1$s§5, %2$s§5, %3$s");
        provider.add("behaviour.start_core.bacteria.affinity_none", "None");
        provider.add("behaviour.start_core.bacteria.input", "§7A Bacteria with any stats & affinities can be inputted");
        provider.add("behaviour.start_core.bacteria.vat_same_output", "§7Replicated bacteria with same stats & affinities as input");
        provider.add("behaviour.start_core.bacteria.vat_mutated_output", "§6Mutated §7bacteria with mutated stats but the same affinities as input");
        provider.add("behaviour.start_core.bacteria.mutator_affinity_output", "§6Mutated §7bacteria with random stats and affinities from input, but same type");
        provider.add("behaviour.start_core.bacteria.mutator_total_output", "§6Mutated §7bacteria with random stats, affinities and type.");
        provider.add("behaviour.start_core.bacteria.mutator_total_output_generic_bacteria", "§3????? Bacteria Colony");

        provider.add("block.start_core.bacteria_multiblock_line", "§3§lBacterial Colonies");
        provider.add("block.start_core.runic_mutator_description", "§7Using runic-age technologies, bacterial colonies are mutated through extreme genetic alterations to their biological structures, either reawakening dormant colonies or reshaping existing ones into novel types.");
        provider.add("block.start_core.breaker_line", "§8-----------------------------------");
        provider.add("block.start_core.parallels", "Can use §3Parallel§r hatches");
        provider.add("block.start_core.rm0", "§eDormant Reawakening:");
        provider.add("block.start_core.rm1", "§7Reawaken dormant colonies into a randomised bacterial colony with random stats, affinities and type.");
        provider.add("block.start_core.gap", "");
        provider.add("block.start_core.rm3", "§bRunic Mutation:");
        provider.add("block.start_core.rm4", "§7Mutate bacterial colonies, changing their stats and affinities randomly along with their type depending on the type of runic plating used.");
        provider.add("block.start_core.harvester_description", "§7A precision bio-extraction device designed to process awakened bacterial colonies. It harnesses all outputs generated by the bacteria, feeding and accelerating their metabolic processes to maximize yield. The extraction process culminates in the bacteria's termination.");
        provider.add("block.start_core.hv0", "§eHydrocarbon Harvesting:");
        provider.add("block.start_core.hv1", "§7Extract primary, secondary, and tertiary outputs from bacterial colonies. §7The specific primary, secondary and tertiary outputs are determined by the §5Affinities §7of the bacteria.");
        provider.add("block.start_core.hv2", "§7The consumption of the bacteria scales with the §fMetabolism §7of the bacteria.");
        provider.add("block.start_core.hv3", "§7The amount fluid of output is base §fProduction§7 of the bacteria multiplied by their individual scalers.");
        provider.add("block.start_core.vat_description", "§7A specialized bioreactor designed for rapid bacterial replication and evolution. It processes a single bacterial sample, outputting duplicates while simultaneously producing mutated variants, fostering both consistency and diversity for experimental or industrial applications.");
        provider.add("block.start_core.vat1", "§eBacterial Breeding:");
        provider.add("block.start_core.vat2", "§7Replicate and mutate bacterial colonies, producing a large amount for usage in Harvesting or Mutation.");
        provider.add("block.start_core.vat3", "§7Produces §f16x Replicated§7 bacteria with the exact same type, stats and affinities.");
        provider.add("block.start_core.vat4", "§7Produces §f16x Mutated§7 bacteria with the exact same type and affinities, but mutated stats depending on the §fMutatability §7of the bacteria.");

        provider.add("tab.start_core.creative", "Star Technology Core");

        provider.add("start_core.machine.auxiliary_boosted_fusion_reactor.description", "§7A high-efficiency fusion reactor that utilises auxiliary fusion coils to enhance fusion reactions, enabling for higher density of fusion reactions taking place within the chamber at once");
        provider.add("start_core.machine.auxiliary_boosted_fusion_reactor.line", "§c§lParallel Fusion");
        provider.add("start_core.machine.auxiliary_boosted_fusion_reactor.fusion_info", "§6Fusion Information:");
        provider.add("start_core.machine.auxiliary_boosted_fusion_reactor.specific", "§7The reactor only takes {0}§7 energy hatches, Each hatch increases the buffer of the reactor by {1}M EU");
        provider.add("start_core.machine.fusion_reactor.overclocking", "§7Overclocks double energy and halve duration.");
        provider.add("start_core.machine.auxiliary_boosted_fusion_reactor.parallel_info", "§eAuxiliary Parallelisation:");
        provider.add("start_core.machine.auxiliary_boosted_fusion_reactor.parallel_info_1", "§7Any Absolute Parallel Mastery Hatches can be used enabling high rates of production.");

        provider.add("gtceu.reflector_fusion_reactor", "Fusion Reactor");
        provider.add("start_core.multiblock.luv_fusion_reactor.description", "The Fusion Reactor MK I is a large multiblock structure used for fusing elements into heavier ones. It can only use §dLuV§r Energy Hatches. For every Hatch it has, its buffer increases by 10M EU, and has a maximum of 160M.");
        provider.add("start_core.multiblock.zpm_fusion_reactor.description", "The Fusion Reactor MK II is a large multiblock structure used for fusing elements into heavier ones. It can only use §cZPM§r Energy Hatches. For every Hatch it has, its buffer increases by 20M EU, and has a maximum of 320M.");
        provider.add("start_core.multiblock.uv_fusion_reactor.description", "The Fusion Reactor MK III is a large multiblock structure used for fusing elements into heavier ones. It can only use §3UV§r Energy Hatches. For every Hatch it has, its buffer increases by 40M EU, and has a maximum of 640M.");
        provider.add("start_core.multiblock.uev_fusion_reactor.description", "The Fusion Reactor MK IV is a large multiblock structure used for fusing elements into heavier ones. It can only use §aUEV§r Energy Hatches. For every Hatch it has, its buffer increases by 160M EU, and has a maximum of 2560M.");

        provider.add("start_core.multiblock.pattern.error.reflector", "§cAll fusion reflectors must be the same§r");
        provider.add("start_core.recipe.min_reflector_tier", "Min. Reflector Tier: §bT%d§r");
        provider.add("start_core.machine.reflector.tooltip_reflector_tier", "Tier §bT{0}§r");
        provider.add("ui.start_core.fusion_reactor.reflector_tier_info", "Reflector Tier: §bT{0}§r");

        provider.add("start_core.machine.parallel_hatch_mk5.tooltip", "Allows to run up to 1024 recipes in parallel.");
        provider.add("start_core.machine.parallel_hatch_mk6.tooltip", "Allows to run up to 2048 recipes in parallel.");
        provider.add("start_core.machine.parallel_hatch_mk7.tooltip", "Allows to run up to 4096 recipes in parallel.");

        provider.add("start_core.machine.absolute_parallel_hatch_mk1.tooltip", "Allows to run up to 4 recipes in parallel.");
        provider.add("start_core.machine.absolute_parallel_hatch_mk2.tooltip", "Allows to run up to 8 recipes in parallel.");
        provider.add("start_core.machine.absolute_parallel_hatch_mk3.tooltip", "Allows to run up to 16 recipes in parallel.");
        provider.add("start_core.machine.absolute_parallel_hatch_energy.tooltip", "§6Without extra energy consumption per parallel.");

        provider.add("block.start_core.dream_link_energy_hatch_tooltip", "§7Wirelessly receiving energy through dreams");
        provider.add("start_core.machine.dream_link.active_network", "§dDreaming on §r{0}");
        provider.add("start_core.machine.dream_link.inactive_network", "§6Awake on §r{0}");
        provider.add("start_core.machine.dream_link.active", "§dDreaming");
        provider.add("start_core.machine.dream_link.not_active", "§6Awake");
        provider.add("start_core.machine.dream_link.network_set_hover", "Identifier of Dream-Network to Transmit/Recieve on");
        provider.add("start_core.machine.dream_link.input_per_sec", "§7EU In: %s §7EU/t");
        provider.add("start_core.machine.dream_link.output_per_sec", "§7EU Out: %s §7EU/t");
        provider.add("start_core.machine.dream_link.total_buffer", "§7Total Energy Buffer:");
        provider.add("start_core.machine.dream_link.total_buffer_value", " %s §7EU");
        provider.add("start_core.machine.dream_link.owner_title", "§7Transmitting to hatches owned by:");
        provider.add("start_core.machine.dream_link.owner", " %s");
        provider.add("start_core.machine.dream_link.owned_title", "§7Dream-link owned by:");
        provider.add("start_core.machine.dream_link.tower.total_buffer_hover", "Total EU stored in the buffers of this tower");
        provider.add("start_core.machine.dream_link.hatch.input_per_sec_hover", "EU transferred to this hatch in the last second expressed in per tick");
        provider.add("start_core.machine.dream_link.tower.input_per_sec_hover", "EU being inputted to this tower in the last second expressed in per tick");
        provider.add("start_core.machine.dream_link.tower.output_per_sec_hover", "EU being transferred from this tower in the last second expressed in per tick");
        provider.add("start_core.machine.dream_link.tower.owner_hover", "EU will only be transmitted to hatches owned by this team/player.");
        provider.add("start_core.machine.dream_link.unlimited_range", "§6Unlimited §7Tower Range");
        provider.add("start_core.machine.dream_link.paragon_range", "§dParagon §7Tower Range");
        provider.add("start_core.machine.dream_link.range", "§7Tower Range: %s §7Blocks");
        provider.add("start_core.machine.dream_link.total_hatches", "§7Transmitting to %s §7Dream-Links.");
        provider.add("start_core.machine.dream_link.tower.range_hover", "The range of this tower in a square radius around (Any height allowed).");
        provider.add("start_core.machine.dream_link.tower.range_button_hover", "Display a render showing the range of the tower");
        provider.add("start_core.machine.dream_link.tower.range_show", "§e[Show Range]");
        provider.add("start_core.machine.dream_link.hatch.owned_hover", "The team/player that owns this hatch.");
        provider.add("start_core.machine.dream_link.connections_display", "§7Connections: %s");
        provider.add("start_core.machine.dream_link.tower.connections_display_hover", "How many Dream-Link receivers are currently connected out of the maximum allowed.");
        provider.add("start_core.machine.dream_link.lucinducer.name", "Lucinducer§o Copied Network §6{0}");
        provider.add("start_core.machine.dream_link.copy_network", "Saved network to Lucinducer");
        provider.add("start_core.machine.dream_link.set_network", "Applied network from Lucinducer");
        provider.add("config.jade.plugin_start_core.dream_link_network_info", "[Star Technology] Dream-Link Network Info");
        provider.add("start_core.dream_link.cover.tooltip", "§7Supplies §f2§eA §f%d (%s§f) §7from the Dream-Network as a cover");
        provider.add("start_core.machine.dream_link_tower.line", "§d§lDream-Link Node");
        provider.add("start_core.machine.dream_link_tower.beam_info", "§bDream-Beam Information:");
        provider.add("start_core.machine.dream_link_tower.beam_description", "§7The node focuses it's beam on a single device until it is full before continuing with the next. Allocation priority is determined based on the proximity of the device to the tower, with closer devices receiving energy first.");
        provider.add("start_core.machine.dream_link_tower.node_info", "§eNode Information:");
        provider.add("start_core.machine.dream_link_tower.range_description", "§7The node has a maximum square-radius of §d{0} §7blocks around it");
        provider.add("start_core.machine.dream_link_tower.connections_description", "§7The node has a maximum connections of §d{0}");
        provider.add("start_core.machine.dream_link_tower.infinite_connections_description", "§7The node has a §dinfinite§7 connections");
        provider.add("start_core.machine.dream_link_tower.copy_description", "§7The dream-network of this node can be copied using a §fLucinducer");
        provider.add("start_core.machine.dream_link_node.description", "§7Focuses a beam of energy through the dreamscape enabling lossless wireless transfer of energy to compatible dream-link devices.");
        provider.add("start_core.machine.oneiric_relay.description", "§7Refines and amplifies dream-energy pathways, enabling a greater distance for beams to travel through the dreamscape.");
        provider.add("start_core.machine.daydream_spire.description", "§7Manipulates semi-lucid thought currents for extended dreamscape energy transmission.");
        provider.add("start_core.machine.beacon_of_lucidity.description", "§7Channels pure lucid clarity to maintain dream-link integrity across the entire dreamscape of a dimension.");
        provider.add("start_core.machine.paragon_of_the_veil.description", "§7The Paragon integrates directly with the substrate of the dream realm itself. Rather than projecting energy, it becomes a universal synchronisation anchor, allowing instantaneous energy transmission to all connected dream-link devices, regardless of dimension.");
        provider.add("start_core.machine.dream_link_tower.beacon_of_lucidity.range_description", "§7The beacon has §dunlimited range §7in the same dimension as it");
        provider.add("start_core.machine.dream_link_tower.paragon_of_the_veil.range_description", "§7The Paragon has §dunlimited cross-dimensional range");

        provider.add("start_core.recipe.temperature", "Temp: 🔥 %sMK");
        provider.add("start_core.recipe.heating_fluid", "Fluid: §6{0}");

        provider.add("behaviour.start_core.hellforge.input_heat", "§fAdds §l%sMK§r§f to the Hadean Crucible");
        provider.add("behaviour.start_core.hellforge.max_heat", "§cMaximum temperature of this heating fluid is §l%sMK");
        provider.add("gtceu.recipe.category.hellforge_heating", "§6Hadean Crucible Heating");
        provider.add("gtceu.hellforge", "§6Infernal Forging");
        provider.add("block.start_core.fornaxs_infernal_rotary_engine_line", "§3§lSovereign of Soulflame");
        provider.add("block.start_core.fornaxs_infernal_rotary_engine_description", "§7An exalted crucible that harnesses the Soul of the Flame to fuel its hellish fury, demanding fluid offerings to further awaken its blazing fury.");
        provider.add("block.start_core.fornaxs_infernal_rotary_engine_d0", "§9Hadean Crucible:");
        provider.add("block.start_core.fornaxs_infernal_rotary_engine_d1", "§7The engine utilises the crucible to alloy materials together at extreme temperatures, but the Soul of the Flame can only heat the crucible to §b925MK§7 itself.");
        provider.add("block.start_core.fornaxs_infernal_rotary_engine_d2", "§7Provide different §dHeating Fluids §7to the engine to heat up the crucible beyond the soul's limit. Each fluid has a maximum temperature that it can heat the crucible to.");
        provider.add("block.start_core.fornaxs_infernal_rotary_engine_d9", "§7These heating recipes will always be prioritised over the other recipes during recipe search.");
        provider.add("block.start_core.fornaxs_infernal_rotary_engine_d3", "§7The engine will always lose §93MK §7of its heat every 10 seconds. When dormant the engine loses an additional §972MK§7 every 10 seconds.");
        provider.add("block.start_core.fornaxs_infernal_rotary_engine_d5", "§5Infernal Parallelisation:");
        provider.add("block.start_core.fornaxs_infernal_rotary_engine_d6", "§7For every n §d450MK§7 over the recipe requirement, the engine gains 2^n absolute parallels.");
        provider.add("ui.start_core.fornaxs_crucible", "§dCrucible Temp: §b{0}MK");

        provider.add("block.start_core.hellforge_added", "Has §bThroughput Boosting§r and §eBatching");
        provider.add("block.start_core.hellforge_multiblock_line", "§c§lDomain of Infernal Reckoning");
        provider.add("block.start_core.hellforge_description", "§7A towering infernal crucible that harnesses the Heart of the Flame to fuel its relentless fires, demanding fluid offerings to awaken its molten fury.");
        provider.add("block.start_core.hellforge_d0", "§6Hadean Crucible:");
        provider.add("block.start_core.hellforge_d1", "§7The hell forge utilises the crucible to alloy materials together at an extreme temperature, but the Heart of the Flame cannot heat the crucible itself.");
        provider.add("block.start_core.hellforge_d2", "§7Provide different §cHeating Fluids §7to the hell forge to heat up the crucible. Each fluid has a maximum temperature that it can heat the crucible to.");
        provider.add("block.start_core.hellforge_d9", "§7These heating recipes will always be prioritised over the other recipes during recipe search.");
        provider.add("block.start_core.hellforge_d3", "§7The hell forge will always lose §95MK §7of its heat every 10 seconds. When dormant the hell forge loses an additional §9120MK§7 every 10 seconds.");
        provider.add("block.start_core.hellforge_d5", "§eInfernal Parallelisation:");
        provider.add("block.start_core.hellforge_d6", "§7For every n §c450MK§7 over the recipe requirement, the hell forge gains 2^n absolute parallels.");
        provider.add("machine.start_core.redstone_interfacing", "§cRedstone Variadic Interfacing:");
        provider.add("block.start_core.hellforge_d8", "§7Provides §fPercentage to nMK §7indicators for each heating fluid temperature cap.");
        provider.add("block.start_core.hellforge_rm", "Has §eBatching");
        provider.add("ui.start_core.hellforge_crucible", "§6Crucible Temp: §c{0}MK");
        provider.add("config.jade.plugin_start_core.hellforge_heat_info", "[Star Technology] Hell Forge Crucible Temp");
        provider.add("config.jade.plugin_start_core.abyssal_harvester_info", "[Star Technology] Abyssal Harvester Entropy");

        provider.add("start_core.redstone_hatch.d0", "Provides access to redstone indicators for multiblock automation");
        provider.add("start_core.redstone_hatch.d1", "§6Functionality depends on the multiblock to interface with");
        provider.add("start_core.redstone_interface.select", "Select Redstone Indicator");
        provider.add("config.jade.plugin_start_core.variadic_redstone_info", "[Star Technology] Variadic Redstone Information");
        provider.add("ui.start_core.redstone_signal", "§6Redstone Level: §c{0}");
        provider.add("ui.start_core.indicator", "§dIndicator: §f%s");
        
        provider.add("start_core.machine.abyssal_harvester.line", "§5§lAbyssal Harvesting");
        provider.add("start_core.machine.abyssal_harvester.description", "§7Absorbs the latent tides of the end abyss, the harvester draws forth exotic abyssal fluids from dimensional fractures and concentrates it as a material fluid.");
        provider.add("start_core.machine.abyssal_harvester.ah0", "§6Abyssal Saturation:");
        provider.add("start_core.machine.abyssal_harvester.ah1", "§7The abyssal harvester will continually become more and more saturated with the latent abyss allowing for different blends of outcomes.");
        provider.add("start_core.machine.abyssal_harvester.ah2", "§7The machine will always gain abyssal saturation at §f0.55% per 5s§7 up to a maximum of §f120.00%§7.");
        provider.add("start_core.machine.abyssal_harvester.ah3", "§7Running recipes in the harvester will decrease the current saturation by §f5.00%§7.");
        provider.add("start_core.machine.abyssal_harvester.ah4", "§6Abyssal Stabilization:");
        provider.add("start_core.machine.abyssal_harvester.ah5", "§7When the Saturation is within a §f+5.00% or -5.00%§7 range of §f22.50%, 52.50%, or 82.50%§7 it gets a free 2x parallel.");
        provider.add("start_core.machine.abyssal_harvester.ah6", "§7Provides §fPercentage to n saturation percent §7indicators for certain n.");
        provider.add("ui.start_core.abyssal_harvester", "Abyssal Saturation:§5 %s%%");
        provider.add("start_core.recipe.min_saturation", "Minimum Saturation:§5 %s");
        provider.add("start_core.recipe.min_saturation.0", "No Minimum Saturation");
        provider.add("start_core.recipe.max_saturation", "Maximum Saturation:§5 %s");
        provider.add("start_core.recipe.max_saturation.0", "No Maximum Saturation");
        provider.add("gtceu.abyssal_harvester", "Abyssal Harvesting");

        provider.add("start_core.recipe.min_vacuum_amount", "Requires Partial Vacuum (§a%s%%§f)");
        provider.add("start_core.recipe.min_vacuum_amount_full", "Requires Full Vacuum (§2%s%%§f)");
        provider.add("start_core.machine.vacuum_pump.tooltip_cap", "§7Capacity: %s");
        provider.add("start_core.machine.vacuum_pump.tooltip_rate", "§7Rate: %s §7(/sec)");
        provider.add("ui.start_core.vcrc.info", "Vacuum Information:");
        provider.add("ui.start_core.vcrc.vacuum_status", "Status: %s");
        provider.add("ui.start_core.vcrc.vacuum_amount", "Percentage: %s (vac%%)");
        provider.add("ui.start_core.vcrc.pump_type.cap", "Capacity: %s");
        provider.add("ui.start_core.vcrc.pump_type.rate", "Rate: %s (/sec)");
        provider.add("ui.start_core.vcrc.vacuum_status.pumping_down", "Pumping Down");
        provider.add("ui.start_core.vcrc.vacuum_status.partial_vacuum", "Partial Vacuum");
        provider.add("ui.start_core.vcrc.vacuum_status.full_vacuum", "Full Vacuum");
        provider.add("ui.start_core.vcrc.vacuum_status.pressure_loss", "Pressure Loss");
        provider.add("ui.start_core.vcrc.vacuum_status.idle", "Idle");
        provider.add("config.jade.plugin_start_core.vacuum_chemical_reaction_chamber_info", "[Star Technology] Vacuum Chemical Reaction Chamber Info");
        provider.add("start_core.multiblock.pattern.error.vacuumpump", "§cAll vacuum pumps must be the same§r");

        provider.add("start_core.machine.sterile_hatch.tooltip", "Makes your Multiblocks extra squeaky clean and sterile!");
        provider.add("start_core.uuid_safe.fail_nearest_player", "Issue regarding UUID's occured, Please visit Dream-Link at %s and consider replacing if Owner is not correct.");
        provider.add("start_core.multiblock.boosted_plasma_turbine.ws2_boosting", "Tungsten Disulfide Boosted");
        provider.add("start_core.multiblock.boosted_plasma_turbine.no_ws2_boosting", "Not Tungsten Disulfide Boosted");
        provider.add("start_core.multiblock.supreme_turbine.ss_h32_boosting", "Superstate Helium 3 Boosted");
        provider.add("start_core.multiblock.nyinsane_turbine.bec_og_boosting", "Oganesson Stabilised Bose-Einstein Condensate Boosted");
        provider.add("start_core.abyssal_containment_room.display_name", "§5True Abyss");
        provider.add("block.start_core.abyssal_containment_room_line", "§5§lThe True Abyss");
        provider.add("start_core.abyssal_containment_room.acr0", "§7You think you peer into its silence... but silence is the tongue of eternity, and §keternity§r§7 has already spoken your name.");
        provider.add("start_core.abyssal_containment_room.acr1", "§dAbyssal Containment");
        provider.add("start_core.abyssal_containment_room.acr2", "§7Provides a simulation of the §5True Abyss §7for all machines placed inside much like a Cleanroom. Due to the nature of the abyss, §fprotection as utilised in the end is advised§7. ");
        provider.add("start_core.abyssal_containment_room.acr3", "§6Isolation Maintenance");
        provider.add("start_core.abyssal_containment_room.acr4", "§7In order to maintain the abyssal containment, §e100000B of End Air§7 and §d50B Dragon Breath§7 per hour must be supplied to the room. ");
        provider.add("start_core.abyssal_containment_room.not_provided_fluids", "Isolation fluids not provided");
        provider.add("start_core.abyssal_containment_room.provided_fluids", "Provided isolation fluids");

        provider.add("item.start_core.lucinducer.tooltip1", "§7Copies and applies Dream-Link network information.");
        provider.add("item.start_core.lucinducer.tooltip2", "§7While held offhand, the stored network will be applied to any placed Dream-Link Hatch or Cover.");
        provider.add("item.start_core.lucinducer.tooltip3", "§7When equipped as a Curio, it charges EU and FE-powered items in your inventory by using the copied Dream-Link network.");

        provider.add("curios.identifier.lucinducer", "Lucinducer");

        provider.add("item.start_core.mechanical_memory_card.tooltip", "§7Allows you to copy miscellaneous machines information. Supported: ");
        provider.add("item.start_core.mechanical_memory_card.tooltip.supported", " - §b%s");

        provider.add("gui.start_core.tooltips.fluid_output_hatches", "Fluid Output Hatches");

        provider.add("start_core.mechanical_memory_card.copy_settings", "Saved settings to Mechanical Memory Card");
        provider.add("start_core.mechanical_memory_card.paste_settings", "Applied settings from Mechanical Memory Card");

        provider.add("block.start_core.threading_controller.tooltip", "Allows you to access and control the threading system by assigning points to various stats.\n§6The available points depend on the helixes you have installed in the multiblock structure.");
        provider.add("start_core.machine.threading_controller.list_components", "§6Threading Components:");
        provider.add("start_core.machine.threading_controller.list_components.hover", "The current amount of each threading component block in this machine");
        provider.add("start_core.machine.threading_controller.component_format", "  %s: %s");
        provider.add("start_core.machine.threading_controller.stat.display_general_remaining", "%s %s §7Remaining");
        provider.add("start_core.machine.threading_controller.stat.general_hover", "These points can be assigned to any others");
        provider.add("start_core.machine.threading_controller.stat.display_assign", "%s %s §7Assigned %s §7Total");
        provider.add("start_core.machine.threading_controller.stat.display", "§6Specialisation:");
        provider.add("start_core.machine.threading_controller.stat.display.hover", "View different points and their current assignments, along with assigning points from Generalis to others");
        provider.add("start_core.machine.threading_controller.speed.pretty_format", "%s%s §7Duration Reduction");
        provider.add("start_core.machine.threading_controller.efficiency.pretty_format", "%s%s §7EU/t Reduction");
        provider.add("start_core.machine.threading_controller.parallels.pretty_format", "%s §7Parallel");
        provider.add("start_core.machine.threading_controller.threading.pretty_format", "%s §7Threads");
        provider.add("start_core.machine.threading_controller.duration.pretty_format", "%s%s §7Duration");
        provider.add("start_core.machine.threading_controller.stat.speed.hover", "0.5^a Modifier per 100 * sum(a) Points. (1 - 0.5^a) * 100 Percent Duration Reduction. Where 'a' is the desired times duration halved");
        provider.add("start_core.machine.threading_controller.stat.efficiency.hover", "(1 - 30/(30 + Efficienta)) * 100 Percent Total Power Reduction");
        provider.add("start_core.machine.threading_controller.stat.parallels.hover", "1 Parallel per 20 Points. Increases duration by Parallel^0.5");
        provider.add("start_core.machine.threading_controller.stat.threading.hover", "1 Thread per 5 Points (Each thread processes a different recipe type)");
        provider.add("start_core.machine.threading_controller.stat.assign", "§e[+]");
        provider.add("start_core.machine.threading_controller.stat.assign.hover", "Assign 1 Generalis to this (SHIFT for 5); (CTRL for 20); (SHIFT + CTRL for max)");
        provider.add("start_core.machine.threading_controller.stat.remove", "§e[-]");
        provider.add("start_core.machine.threading_controller.stat.remove.hover", "Unassign 1 Generalis from this (SHIFT for 5); (CTRL for 20); (SHIFT + CTRL for max)");
        provider.add("start_core.machine.threading.stat.general", "Generalis");
        provider.add("start_core.machine.threading.stat.speed", "Velocitas");
        provider.add("start_core.machine.threading.stat.efficiency", "Efficienta");
        provider.add("start_core.machine.threading.stat.parallels", "Parallelismus");
        provider.add("start_core.machine.threading.stat.threading", "Filum");
        provider.add("start_core.machine.threading_controller.header", "Prismatic Threading Stats:");
        provider.add("start_core.machine.threading_controller.active_threads", "Active Threads:");
        provider.add("start_core.machine.threading_controller.thread_header", "§7Thread %s:");
        provider.add("start_core.machine.threading_controller.jade_thread_header", "§6Thread %s§e:");
        provider.add("start_core.machine.threading_controller.threads_available", "%s §7Thread/s available");
        provider.add("config.jade.plugin_start_core.threading_recipes", "[Star Technology] Threading Recipes");

        provider.add("block.start_core.helix_tooltip_title", "§6Prismatic Threading Helix");
        provider.add("block.start_core.stat.general.display", "§7%s: §f%s");
        provider.add("block.start_core.stat.speed.display", "§7%s: §a%s");
        provider.add("block.start_core.stat.efficiency.display", "§7%s: §d%s");
        provider.add("block.start_core.stat.parallels.display", "§7%s: §c%s");
        provider.add("block.start_core.stat.threading.display", "§7%s: §9%s");
        provider.add("config.jade.plugin_start_core.threading_stat_blocks", "[Star Technology] Threading Stat Block");

        provider.add("block.start_core.t1_combustion_module_description", "Unreal Chemical Combustion");
        provider.add("block.start_core.t1_combustion_module_d0", "§eBase Production: §r%s EU/t");
        provider.add("block.start_core.t1_combustion_module_d1", "Uses §f%d B/hr §7of §6Lubricant §7while working");
        provider.add("block.start_core.t1_combustion_module_d2", "Supply §f80 mB/s§7 of §bWhite Fuming Nitric Acid §7to produce up to §f%s EU/t§7 at §f2x§7 fuel consumption.");

        provider.add("block.start_core.t2_combustion_module_description", "Supreme Chemical Combustion");
        provider.add("block.start_core.t2_combustion_module_d0", "§eBase Production: §r%s EU/t");
        provider.add("block.start_core.t2_combustion_module_d1", "Uses §f%d B/hr §7of §6Lubricant §7while working");
        provider.add("block.start_core.t2_combustion_module_d2", "Supply §f120 mB/s§7 of §bRed Fuming Nitric Acid §7to produce up to §f%s EU/t§7 at §f2x§7 fuel consumption.");

        provider.add("block.start_core.t1_rocket_module_description", "Supreme Rocket Combustion");
        provider.add("block.start_core.t1_rocket_module_d0", "§eBase Production: §r%s EU/t");
        provider.add("block.start_core.t1_rocket_module_d1", "Uses §f%d B/hr §7of §6Tungsten Disulfide §7while working");
        provider.add("block.start_core.t1_rocket_module_d2", "Supply §f210 mB/s§7 of §bDioxygen Difluoride §7to produce up to §f%s EU/t§7 at §f2x§7 fuel consumption.");

        provider.add("block.start_core.t2_rocket_module_description", "Nyinsane Rocket Combustion");
        provider.add("block.start_core.t2_rocket_module_d0", "§eBase Production: §r%s EU/t");
        provider.add("block.start_core.t2_rocket_module_d1", "Uses §f%d B/hr §7of §6Tungsten Disulfide §7while working");
        provider.add("block.start_core.t2_rocket_module_d2", "Supply §f240 mB/s§7 of §bFerrocenium Superoxide §7to produce up to §f%s EU/t§7 at §f2x§7 fuel consumption.");

        provider.add("start_core.multiblock.boosted_combustion_oxidizer_t1", "§bWhite Fuming Nitric Acid boosted");
        provider.add("start_core.multiblock.boosted_combustion_oxidizer_t2", "§bRed Fuming Nitric Acid boosted");
        provider.add("start_core.multiblock.boosted_combustion_oxidizer_t3", "§bDioxygen Difluoride boosted");
        provider.add("start_core.multiblock.boosted_combustion_oxidizer_t4", "§bFerrocenium Superoxide boosted");

        provider.add("start_core.multiblock.frame.linked_modules", "Linked Modules: %s");

        provider.add("start_core.multiblock.frame.cooled_deionized", "§7Coolant: §bDe-Ionized Water");
        provider.add("start_core.multiblock.frame.cooled_distilled", "§7Coolant: §1Distilled Water");
        provider.add("start_core.multiblock.frame.not_cooled", "§7Coolant: §cNone");
        provider.add("start_core.multiblock.frame.coolant_consumption", "Coolant Usage: %s B/h");

        provider.add("block.start_core.modular_combustion_frame_description", "§6§lModular Combustion Complex");
        provider.add("block.start_core.modular_combustion_frame_d1", "§7A Modular Frame that can hold up to 8 different Combustion or Rocket Modules");
        provider.add("block.start_core.modular_combustion_frame_d2", "§3Cooling Boosting: §7Consumes 500B an hour per module connected of §fDistilled Water§7 for a §a+20% §fEU/t §7output, or with §fDe-Ionized Water§7 for a §a+40% §fEU/t §7output, and a providing no coolant leads to a §4-10% §fEU/t §7to the energy output");
        provider.add("block.start_core.modular_combustion_frame_d3", "§cModular Frame: §7Place the Module so its §fTerminal Hatch §7faces directly into a §fNode Hatch §7on the Multiblock. The §fTerminal Hatch §7UI will show whether the connection is active and which Multiblock this Module connects to. The §fFrame §7enables all §fModule Machines §7attached to it.");
        provider.add("block.start_core.modular_combustion_frame_tooltip_laser", "Can use §dLaser Hatches");

        provider.add("start_core.multiblock.frame.avg_eu_in", "§eAverage EU In: §a%s EU/t");
        provider.add("start_core.multiblock.frame.frame_boost", "§aFrame Modifier %s%%");
        provider.add("start_core.multiblock.frame.boosted_eu_out", "§eBoosted EU Out: §c%s EU/t");

        provider.add("modular.start_core.scaled_voltage", "Scaled Voltage: %s");
        provider.add("modular.start_core.scaled_amperage", "Scaled Amperage: %s");
        provider.add("modular.start_core.no_scaling", "No Energy Scaling Applied");
        provider.add("modular.start_core.no_link", "No established Modular-Link");
        provider.add("modular.start_core.has_link", "Modular-Link established");
        provider.add("modular.start_core.supported_list_title", "Supported Modules:");
        provider.add("modular.start_core.not_formed", "Structure not formed");
        provider.add("modular.start_core.linked_type", "Linked Module:");
        provider.add("modular.start_core.not_formed_description", "The multiblock must be formed for a link to be established.");
        provider.add("modular.start_core.supported_list_description", "These are the available multiblock types which can be linked with this terminal to enable their functionality.");
        provider.add("config.jade.plugin_start_core.modular_interface_hatch_part_machine", "[Star Technology] Modular Interface Information");
        provider.add("config.jade.plugin_start_core.fusion_reactor_info", "[Star Technology] Fusion Reactor Info");

        provider.add("variadic.start_core.indicator.default", "Click here to select (if any)");
        provider.add("variadic.start_core.description.default", "When part of a formed multiblock which has interfaced with this hatch, clicking here will produce a dropdown to select a desired redstone indicator of the multiblock's function");
        provider.add("variadic.start_core.indicator.hellforge", "Percentage to §c%s");
        provider.add("variadic.start_core.description.hellforge", "A clamped value between 0-15 of the (current temperature / %sMK) * 15");
        provider.add("variadic.start_core.indicator.abyssal_harvester", "Ratio of §5%s §rAbyssal Saturation");
        provider.add("variadic.start_core.description.abyssal_harvester", "A clamped value between 0-15 of the (current saturation / %s%%) * 15");

        provider.add("block.start_core.komaru.tooltip1", "§6§lModular Gateway of the Heavens");

        provider.add("block.start_core.komaru.paginated1.1", "§7To access the worlds beyond this reality the draconic empires utilized all understanding of technology. From that, Dr.Conic and the surrounding visionaries were able to construct a gateways to heavens beyond their understanding. By honing §dFaetic§7 and §5Draconic§7 powers they were able to find fractal like spatial realms which largely stood outside of time. They harvested them to fuel their own growth... and their greed had a grave cost. For the energies utilized to break into realms beyond, are the same ones that can shatter a world completely and leave it to be consumed by the abyss. Forever marking a world with a reminder of what happens when trying to control powers barely understood.");
        provider.add("block.start_core.komaru.paginated1.2", "§7If one is to understand and stabilize these powers then reward, not disaster awaits. Harvesting what the Draconic Empires could never is a blessing and a curse just the same. §kkomaru cat§7 awaits what you decide next.");

        provider.add("block.start_core.komaru.paginated2.1", "§bModular Overclocking");
        provider.add("block.start_core.komaru.paginated2.2", "§7Each §6K.O.M.A.R.U§7 has the ability to store 4 Advanced Modules and 20 Basic Modules. Each Basic Module is overclocked to 1/64th of the amperage input up to §f64§eA§7. Each Advanced Module is overclocked to 1/16th of the amperage input up to §f256§eA§7. To achieve this §6K.O.M.A.R.U§7 accepts §dLaser Hatches§7.");
        provider.add("block.start_core.komaru.paginated2.3", "§d§lFaetic Scaling");
        provider.add("block.start_core.komaru.paginated2.4", "§7Compromised of the product of §dFaetic Scaling§7 and §9Modular Efficiency§7 where:");
        provider.add("block.start_core.komaru.paginated2.5", "    §dFaetic Scaling§7 = 1.025 ^ ([Number of Basic Modules] + 3 * [Number of Advanced Modules])");
        provider.add("block.start_core.komaru.paginated2.6", "    §9Modular Efficiency§7 = 0.99 ^ ([Number of Basic Modules]) * 0.98 ^ ([Number of Advanced Modules])");

        provider.add("block.start_core.komaru.paginated3.1", "§3§lFilament Stabilization");
        provider.add("block.start_core.komaru.paginated3.2", "§7By using higher tiers of filaments you can more effectively stabilize the riftic connection but the frame will consume all available filaments it can per cycle (10s).");
        provider.add("block.start_core.komaru.paginated3.3", "    §3Filament Stabilization§7 = (1 - 0.025 * [Tier of Filament]) ^ ([Filaments Available] / 5)");
        provider.add("block.start_core.komaru.paginated3.4", "§f§lFaematter Consumption");
        provider.add("block.start_core.komaru.paginated3.5", "§7To enable the modules to run and the riftic connection the frame requires a constant supply of Faematter to run that if unchecked will increase exponentially.");
        provider.add("block.start_core.komaru.paginated3.6", "§7The amount of Faematter consumed floors at 10B per cycle and ceilings at 50B per cycle.");
        provider.add("block.start_core.komaru.paginated3.7", "    §fFaematter Consumption§7 = [Consumption of Faematter in Prior Cycle] * §3Filament Stabilization§7 * §dFaetic Scaling");
        provider.add("block.start_core.komaru.paginated3.8", "    §7If this is the first cycle the minimum of 10B will be assumed as the prior cycle consumption");

        provider.add("block.start_core.komaru.paginated4.1", "§4§lDire Consequences");
        provider.add("block.start_core.komaru.paginated4.2", "§7The §6K.O.M.A.R.U§7 should not be unchecked as it scales the power it demands by the faematter it consumes.");
        provider.add("block.start_core.komaru.paginated4.3", "- §7If stabilized you will be awarded power §aefficiency§7.");
        provider.add("block.start_core.komaru.paginated4.4", "- §7If left unchecked you will face §4dire consequences§7.");
        provider.add("block.start_core.komaru.paginated4.5", "§ePower Consumption§7 = §fFaematter Consumption§7 / 11");

        provider.add("start_core.machine.komaru.stabilization_unpaid", "KOMARU stabilization payment missing");
        provider.add("ui.start_core.komaru.stabilization", "KOMARU Stabilization:");
        provider.add("ui.start_core.komaru.c", "C: %s buckets/cycle");
        provider.add("ui.start_core.komaru.previous_c", "Previous C input: %s buckets");
        provider.add("ui.start_core.komaru.modules", "Linked modules: %s basic, %s advanced");
        provider.add("ui.start_core.komaru.required_faematter", "Required faematter: %s mB (%s buckets)");
        provider.add("ui.start_core.komaru.consumed_faematter", "Last faematter consumed: %s mB (%s buckets)");
        provider.add("ui.start_core.komaru.fae_scaling", "Fae Scaling: %s");
        provider.add("ui.start_core.komaru.filaments", "Filament input: %s (%s total, highest tier %s)");
        provider.add("ui.start_core.komaru.scaling", "Scaling: modules %s x filaments %s x fae %s");
        provider.add("ui.start_core.komaru.eu_multiplier", "Module EU/t multiplier: %sx");
        provider.add("ui.start_core.komaru.stabilization_paid", "§aStabilization paid");
        provider.add("ui.start_core.komaru.stabilization_unpaid", "§cStabilization unpaid");
        provider.add("variadic.start_core.indicator.komaru.c", "Ratio of §bC §rto §e%s");
        provider.add("variadic.start_core.description.komaru.c", "A clamped value between 0-15 of (current C / %s) * 15");
        provider.add("variadic.start_core.indicator.komaru.fae_scaling", "Ratio of §dFae Scaling §rto §e%s");
        provider.add("variadic.start_core.description.komaru.fae_scaling", "A clamped value between 0-15 of (current Fae Scaling / %s) * 15");

        provider.add("start_core.parallel_hatch.max_parallel", "Maximum Allowed Parallels");
        provider.add("start_core.parallel_hatch.min_parallel", "Minimum Allowed Parallels");
        provider.add("start_core.parallel_hatch.jade_min_parallel", "Requires at least %d Recipes in Parallel");
        provider.add("config.jade.plugin_start_core.min_parallel", "[Star Technology] Minimum Parallel Info");

        provider.add("variadic.start_core.indicator.vcrc.vac_to_capacity", "Percentage to Vac%% Capacity");
        provider.add("variadic.start_core.description.vcrc.vac_to_capacity", "A clamped value between 0-15 of the (current vac%% / %s%%) * 15");

        provider.add("block.start_core.solar_cell_line", "§lPhotovoltaic Energy Component");

        provider.add("solar.start_core.solar_cell.tooltip1", "§7Produces energy during the day, while heating up.");
        provider.add("solar.start_core.solar_cell.tooltip2", "§7Cools down during the night.");
        provider.add("solar.start_core.solar_cell.tooltip3", "§7Produces power only when used in a Solar Panel or Array.");

        provider.add("solar.start_core.solar_cell.temperature_scale_tooltip", "§9Heating Modifier:§f %sx");
        provider.add("solar.start_core.solar_cell.temperature_tooltip", "§cTemperature:§f %s/%s K");
        provider.add("solar.start_core.solar_cell.durability_tooltip", "§dDurability:§f %s/%s");
        provider.add("solar.start_core.solar_cell.is_broken", "§4Cell is broken");
        provider.add("config.jade.plugin_start_core.solar_cells", "[Star Technology] Solar Cell Block");

        provider.add("block.start_core.solar_machine.tooltip0", "§lPhotovoltaic Energy Production");
        provider.add("block.start_core.solar_machine.tooltip1", "§7Use §fSolar Cells§7 to harvest the power of the §eSUN§7. This machine will combine the produced power of all connected solar cells and replace broken ones when they decay if you supply it with new ones.");
        provider.add("block.start_core.solar_machine.tooltip2", "§fSolar Cells§7 will only produce power if they can see the sun.");

        provider.add("block.start_core.solar_machine.paginated1.1", "§a§lVoltage multiplier:§f x%s");
        provider.add("block.start_core.solar_machine.paginated1.2", "§7Total EU/t generation is increased by this factor.");

        provider.add("block.start_core.solar_machine.paginated1.3", "§c§lSolar Cell Decay");
        provider.add("block.start_core.solar_machine.paginated1.4", "§7Solar cells complete a cycle every 6 seconds, after which they will lose a part of their durability, depending on their heat.");

        provider.add("block.start_core.solar_machine.paginated2.1", "§4§lOverheating");
        provider.add("block.start_core.solar_machine.paginated2.2", "§7Each Solar Cell has its own max temperature, and have a minimum temperature of §f273K§7.");
        provider.add("block.start_core.solar_machine.paginated2.3", "§4Overheat§f%§7 = §f[Current Temperature - 273]§7 / §f[Max Temperature - 273]");
        provider.add("block.start_core.solar_machine.paginated2.4", "§7Based on the §4overheat§f%§7 durability loss per cycle is increased, with the following multipliers:");
        provider.add("block.start_core.solar_machine.paginated2.5", "    §f<70%§7 = 1x Durability loss per cycle");
        provider.add("block.start_core.solar_machine.paginated2.6", "    §f<80%§7 = 2x Durability loss per cycle");
        provider.add("block.start_core.solar_machine.paginated2.7", "    §f<90%§7 = 4x Durability loss per cycle");
        provider.add("block.start_core.solar_machine.paginated2.8", "    §f>90%§7 = 8x Durability loss per cycle");

        provider.add("block.start_core.solar_panel.paginated1.1", "§e§lSolar Cell Thermodynamics");
        provider.add("block.start_core.solar_panel.paginated1.2", "§7Each cell will increase its temperature by §f0.2K§7 per 6 second cycle while producing power.");
        provider.add("block.start_core.solar_panel.paginated1.3", "§7Each cell will decrease its temperature by §f0.1K§7 per 6 second cycle while inactive.");

        provider.add("block.start_core.solar_array.paginated1.1", "§e§lSolar Cell Thermodynamics");
        provider.add("block.start_core.solar_array.paginated1.2", "§7Each cell will increase its temperature by §f0.3K§7 per 6 second cycle while producing power.");
        provider.add("block.start_core.solar_array.paginated1.3", "§7Each cell will decrease its temperature by §f0.1K§7 per 6 second cycle while inactive.");

        provider.add("block.start_core.solar_array.paginated2.1", "§3§lSolar Array Cooling");
        provider.add("block.start_core.solar_array.paginated2.2", "§7By providing §f%s§7 buckets of §f%s§7 every hour the array will become cooled.");
        provider.add("block.start_core.solar_array.paginated2.3", "§7While cooled solar cell thermodynamics will be adjusted.");
        provider.add("block.start_core.solar_array.paginated2.4", "§7Each cell will increase its temperature by §f0.18K§7 per 6 second cycle while producing power.");
        provider.add("block.start_core.solar_array.paginated2.5", "§7While cooled the solar array will increase its voltage multiplier to §fx%s");

        provider.add("solar.start_core.solar_array.tooltip1", "Can use §dLaser Hatches");

        provider.add("solar.start_core.solar_machine.cell_tooltip", "Working Cells: %s/%s");
        provider.add("solar.start_core.solar_machine.avg_temp_tooltip", "§cAverage Temperature:§f %s K");
        provider.add("solar.start_core.solar_machine.avg_dura_tooltip", "§dAverage Durability:§f %s");
        provider.add("solar.start_core.solar_machine.is_cooled_tooltip", "§3Actively cooled");
        provider.add("solar.start_core.solar_machine.is_not_cooled_tooltip", "§1Not actively cooled");
        provider.add("config.jade.plugin_start_core.solar_machine_info", "[Star Technology] Solar Panel/Array Entropy");

        provider.add("variadic.start_core.indicator.solar_machine", "Percentage to %s K");
        provider.add("variadic.start_core.description.solar_machine", "A clamped value between 0-15 of the (avg temp / %s K) * 15 (In Celsius, K - 273)");
    }
}
