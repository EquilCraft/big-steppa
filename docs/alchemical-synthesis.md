# Алхимический синтез руд

Настройки создаются в основном конфиге `bigsteppa.cfg`.

## Руды

Формат элемента `alchemical_synthesis.ores.definitions`:

```text
id;modid:block;meta;baseWeight;minVein;maxVein;aspect=amount,aspect=amount
```

Пример:

```text
thaumcraft.infused_air;Thaumcraft:blockCustomOre;1;8;2;5;aer=2,praecantatio=1
```

- `baseWeight` — относительный вес руды при случайном выборе.
- `minVein` и `maxVein` — размер синтезируемой жилы до бонусов рун.
- Стоимость аспектов указана за один поставленный блок.
- Неизвестные блоки, аспекты и некорректные строки пропускаются с ошибкой в логе.

Стандартные веса основаны на количестве попыток и размере жил vanilla/Thaumcraft worldgen.

## Руны

Формат элемента `alchemical_synthesis.runes.definitions`:

```text
runeId;maxEffectiveCount;extraBlocksPerRune;speedPercentPerRune;oreId=weightBonus,...;aspect=amount,...
```

Пример:

```text
air;8;0;0;thaumcraft.infused_air=20;aer=1
abundance;8;1;0;;permutatio=1,terra=1
speed;8;0;15;;motus=1,potentia=1
```

- `maxEffectiveCount` ограничивает число одновременно учитываемых рун типа.
- `extraBlocksPerRune` увеличивает размер одной жилы.
- `speedPercentPerRune` ускоряет синтез и всасывание аспектов.
- `weightBonus` прибавляется к весу указанной руды за каждую активную руну.
- Стоимость рун списывается за каждый эффективный уровень при каждом успешном синтезе.

Доступные `runeId`: `basic`, `reinforced`, `resonant`, `stabilized`, `abundance`, `speed`,
`air`, `fire`, `water`, `earth`, `order`, `entropy`.

Руды ставятся только в воздухе внутри объёма `X/Z: -4..4`, `Y: -6..-1`
относительно ядра. Существующие блоки никогда не заменяются.

`AspectInput` напрямую высасывает essentia из банок в радиусе 12 блоков и хранит
не более `100` единиц каждого отдельного аспекта. Трубы не поддерживаются. Core
ничего не хранит и списывает стоимость синтеза непосредственно из `AspectInput`.
Содержимое показывается стандартным HUD очков откровения Thaumcraft только при
наведении на `AspectInput`.
