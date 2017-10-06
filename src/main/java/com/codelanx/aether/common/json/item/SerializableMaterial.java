package com.codelanx.aether.common.json.item;

import com.codelanx.aether.common.cache.query.LocatableInquiry;
import com.codelanx.aether.common.cache.query.MaterialInquiry;
import com.codelanx.commons.data.FileSerializable;
import com.runemate.game.api.hybrid.entities.definitions.ItemDefinition;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class SerializableMaterial implements FileSerializable, Material {

    private final Boolean stackable;
    private final Boolean equippable;
    private final String name;
    private final int id;
    private final AtomicReference<MaterialInquiry> inq = new AtomicReference<>();
    private final AtomicReference<LocatableInquiry> groundItem = new AtomicReference<>();

    public SerializableMaterial(String name, int id, boolean stacks, boolean equips) {
        this.name = name;
        this.id = id;
        this.stackable = stacks;
        this.equippable = equips;
    }

    protected SerializableMaterial(Map<String, Object> serialized) {
        this.name = (String) serialized.get("name");
        this.id = ((Long) serialized.get("id")).intValue();
        this.stackable = (Boolean) serialized.get("stackable");
        this.equippable = (Boolean) serialized.get("equippable");
    }

    public SerializableMaterial(Material other) {
        this.name = other.getName();
        this.equippable = other.isEquippable();
        this.stackable = other.isStackable();
        this.id = other.getId();
    }

    public SerializableMaterial(ItemDefinition other) {
        this.name = other.getName();
        this.id = other.getId();
        this.stackable = other.stacks();
        this.equippable = other.isEquipable();
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> back = new HashMap<>();
        back.put("name", this.name);
        back.put("id", this.id);
        back.put("stackable", this.stackable);
        back.put("equippable", this.equippable);
        return back;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean isStackable() {
        return this.stackable;
    }

    @Override
    public boolean isEquippable() {
        return this.equippable;
    }

    @Override
    public AtomicReference<LocatableInquiry> getReferenceGroundItemInquiry() {
        return this.groundItem;
    }

    @Override
    public AtomicReference<MaterialInquiry> getReferenceToInquiry() {
        return this.inq;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SerializableMaterial that = (SerializableMaterial) o;

        if (getId() != that.getId()) return false;
        if (isStackable() != that.isStackable()) return false;
        if (isEquippable() != that.isEquippable()) return false;
        return getName().equals(that.getName());
    }

    @Override
    public int hashCode() {
        int result = Boolean.hashCode(isStackable());
        result = 31 * result + Boolean.hashCode(isEquippable());
        result = 31 * result + getName().hashCode();
        result = 31 * result + getId();
        return result;
    }

    @Override
    public String toString() {
        return "SerializableMaterial{" +
                "stackable=" + stackable +
                ", equippable=" + equippable +
                ", name='" + name + '\'' +
                ", id=" + id +
                '}';
    }
}
