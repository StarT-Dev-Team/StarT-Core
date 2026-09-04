# Star Technology Core

**The core mod for Star Technology and a GregTech CEu Modern addon for Minecraft 1.20.1** providing advanced machinery, and unique gameplay systems.

## Overview

Star Technology Core is the foundation mod for the **Star Technology modpack**, built as a direct addon to [GregTech CEu Modern -  StarT Fork](https://github.com/StarT-Dev-Team/GTM-StarT-Fork). It adds several custom machines, powerful processing systems, and features like bacteria, wireless energy, modular combustion, K.O.M.A.R.U. and boosted plasma turbines.

---

## Key Features

### Custom Machines

#### **Bacteria Systems**
- **Bacterial Breeding Vat** - Selectively breed bacteria for desired properties
- **Runic Mutator** - Apply runic essence to create new bacterial variants
- **Hydrocarbon Harvester** - Extract valuable hydrocarbons from bacterial colonies

#### **Power & Energy**
- **Modular Combustion Engine** - Configurable power generation with fuel flexibility
- **Solar Machines** - Multiple solar energy collection variants
- **Advanced Converters** - Efficient power conversion between systems

#### **Wireless Energy**
- **Dreamlink Transmitter Towers** - energy transmission network
- **Dreamlink Hatches** - Integrated wireless connectivity for machines

#### **Processing & Transformation**
- **Reflector Fusion Reactor** - fusion reactors with reflector-based tier scaling
- **Hellforge** - Extreme-temperature processing chamber with custom mechanics
- **Vacuum Chemical Reactor** - Chemical reactor, but *vacuum*
- **Drilling Rig IV** - A new drilling rig tier
- **Threading** - Stat based machines with support for multithreading
- **Abyssal Containment Unit** - Specialized cleanroom

#### **Storage & Interface**
- **More Drums & Crates** - Enriched Naquadah and Neutronium crates, drums and cells
- **Redstone Variadics** - Rule-based and machine-based redstone signals
- **HPCA Components** - A new passive cooling and a computation component

### **Recipe modfiers**
- **Throughput boosting** - runs 4 recipes in parallel with some tweaks to duration and energy usage
- **Bulking** - runs many recipes in parallel

### Specialized Items

| Item | Research | Purpose |
|------|----------|---------|
| **Data DNA Disk** | 320 CWU/t | Higher tier research medium |
| **Component Data Core** | 500 CWU/t | Highest tier research medium |
| **Lucinducer** | - | Curio item; enables dreamlink copying and inventory charging |
| **Mechanical Memory Card** | - | Copy/paste machine configurations |

### Advanced Capabilities

Introduces specialized GTCEu machine hatches and parts:
- **Absolute Parallel Hatch** - Unlimited parallelization
- **Modular Terminals/Nodes** (2A to 4096A) - Graduated power tier support
- **Vacuum Pump Hatch** - Low-pressure environment management
- **Threading Controller** - Parallel processing orchestration

---

## ⚙️ Configuration

### Komaru Renderer Toggle

The mod supports an optional Komaru rendering system. Configure in your mod config:

```properties
# Star Technology Core config
# Toggle Komaru renderer (requires Embeddium for best results)
komaru_renderer_enabled=true
```

---

## Development

To pull the latest GTm snapshot, when building add the `-Pgtceu_snapshot=true` flag.
For example: `./gradlew build -Pgtceu_snapshot=true`

---

## Project Structure

```
src/main/java/com/startechnology/start_core/
├── StarTCore.java                    # Main mod entry point
├── StarTCoreGTAddon.java             # GTm addon implementation
├── StarTCoreClient.java              # Client-side code
├── StarTConfig.java                  # Configuration
├── machine/                          # Custom machine implementations
├── item/                             # Custom items
├── materials/                        # Custom materials
├── recipe/                           # Custom recipe types
├── api/                              # APIs (bacteria, dreamlink, fusion)
├── block/                            # Custom blocks
├── integration/                      # Mod integrations (KubeJS, JEI, EMI, Jade, Ponder)
└── capability/                       # Custom GT capabilities

src/main/resources/
├── assets/start_core/               # Textures, models, sounds, shaders
├── data/start_core/                 # Curio slot for lucinducer
└── start_core.mixins.json           # Mixin configurations
```

### KubeJS Integration

Provides custom KubeJS bindings:
- **PonderPalette** - Define custom ponder colors
- **PonderPointing** - Create pointing instructions
- **PonderTickingInstruction** - Add timed animations
- **Fusion Reflector Block Builder** - Define custom reflector recipes

---

## ❤️ Thanks

- [Lytho](https://github.com/AlmostReliable/ponderjs) for PonderJS that heavily inspired the KubeJS integration of
  Ponder in this mod.
- [ChAoS_UnItY_](https://github.com/ChAoSUnItY/Create-Ultimine) and
  [alegiannx](https://github.com/alegian/framed-blocks-ultimine) for the FTB Ultimine integrations that heavily inspired
  the ones in this mod.
- [DestroyerMob](https://github.com/DestroyerMob) for making the patch of Effortless Buidling that fixed a long standing 
  bug with block placement in survival mode, that we integrated in this mod.

---

## 📄 License

This mod is licensed under the **LGPLv3.0** License. See [LICENSE](LICENSE) for details.
