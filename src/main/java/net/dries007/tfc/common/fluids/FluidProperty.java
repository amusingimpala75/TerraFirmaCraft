/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.fluids;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.minecraft.fluid.Fluid;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.Lazy;
import net.minecraft.util.registry.Registry;

public class FluidProperty extends Property<FluidProperty.FluidKey>
{
    public static FluidProperty create(String name, Stream<Object> fluids)
    {
        return new FluidProperty(name, fluids.map(obj -> {
            if (obj instanceof Identifier)
            {
                return (Identifier) obj; // Direct references to fluid IDs are allowed
            }
            else if (obj instanceof Fluid)
            {
                return getFromRegistry((Fluid) obj); // Vanilla fluids are allowed
            }
            else if (obj instanceof TFCFluids.FluidPair<?>)
            {
                return getFromRegistry(((TFCFluids.FluidPair<?>) obj).getSecond()); // Fluid pairs are allowed (we know how to obtain the ID from it without loading the fluid)
            }
            else
            {
                throw new IllegalArgumentException("FluidProperty#create called with a weird thing: " + obj);
            }
        }));
    }

    private final Map<String, FluidKey> valuesById;
    private final Map<Fluid, FluidKey> cachedValues;
    private final Set<FluidKey> values;
    private final Lazy<Set<Fluid>> fluids;

    protected FluidProperty(String name, Stream<Identifier> fluids)
    {
        super(name, FluidKey.class);

        this.valuesById = fluids.collect(Collectors.toMap(Identifier::getPath, FluidKey::new));
        this.cachedValues = new HashMap<>();
        this.values = new HashSet<>(this.valuesById.values());
        this.fluids = new Lazy<>(() -> this.values.stream().map(FluidKey::getFluid).collect(Collectors.toSet()));
    }

    public boolean canContain(Fluid fluid)
    {
        return fluids.get().contains(fluid);
    }

    public Collection<Fluid> getPossibleFluids()
    {
        return fluids.get();
    }

    public FluidKey keyFor(Fluid fluid)
    {
        FluidKey key = cachedValues.get(fluid);
        if (key != null)
        {
            return key;
        }
        key = valuesById.get(Objects.requireNonNull(getFromRegistry(fluid)).getPath());
        if (key == null)
        {
            throw new IllegalArgumentException("Tried to get the FluidKey for a fluid [" + getFromRegistry(fluid) + "] which was not present in property " + getName() + " / " + getValues());
        }
        cachedValues.put(fluid, key);
        return key;
    }

    @Override
    public Collection<FluidKey> getValues()
    {
        return values;
    }

    @Override
    public String name(FluidKey value)
    {
        return value.name.getPath();
    }

    @Override
    public Optional<FluidKey> parse(String value)
    {
        return Optional.ofNullable(valuesById.get(value));
    }

    public static class FluidKey implements Comparable<FluidKey>
    {
        private final Identifier name;
        private final Fluid fluid;

        private FluidKey(Identifier name)
        {
            this.name = name;
            this.fluid = Registry.FLUID.get(name);
        }

        public Fluid getFluid()
        {
            return fluid;
        }

        @Override
        public int compareTo(FluidKey other)
        {
            return name.compareTo(other.name);
        }

        @Override
        public String toString()
        {
            return "FluidKey[" + name + ']';
        }
    }

    public static Identifier getFromRegistry(Fluid fluid) {
        return Registry.FLUID.getId(fluid);
    }
}
