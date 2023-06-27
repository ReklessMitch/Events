package me.reklessmitch.csgo.configs;

import com.massivecraft.massivecore.store.Entity;
import lombok.Getter;
import lombok.Setter;
import me.reklessmitch.csgo.colls.ArenaColl;
import me.reklessmitch.csgo.utils.SerLocation;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Arena extends Entity<Arena> {

    String name = "";
    boolean active = false;
    List<String> allowedGames = new ArrayList<>();
    List<String> allowedKits = new ArrayList<>();
    List<SerLocation> spawnPoints = new ArrayList<>();
    List<SerLocation> team1Spawns = new ArrayList<>();
    List<SerLocation> team2Spawns = new ArrayList<>();
    List<SerLocation> removeFloor = new ArrayList<>();
    List<SerLocation> floors = new ArrayList<>();

    public static Arena get(Object oid) {
        return ArenaColl.get().get(oid);
    }

    @Override
    public Arena load(@NotNull Arena that)
    {
        super.load(that);
        return this;
    }

    public SerLocation getSpawnPoint(){
        if(spawnPoints.isEmpty()){
            return null;
        }
        return spawnPoints.get(0);
    }

    public void addSpawnLocation(SerLocation location) {
        spawnPoints.add(location);
        this.changed();
    }

    public void addSpawnLocation(int team, Location location) {
        if(team == 1){
            team1Spawns.add(new SerLocation(location));
        }else if(team == 2){
            team2Spawns.add(new SerLocation(location));
        }
        this.changed();
    }

    public void addFloor(Location location) {
        floors.add(new SerLocation(location));
    }

    public void addRemoveFloor(Location location) {
        removeFloor.add(new SerLocation(location));
    }
}
