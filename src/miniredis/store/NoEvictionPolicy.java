package miniredis.store;

import java.util.Optional;

public class NoEvictionPolicy<K> implements EvictionPolicy<K>{
    @Override
    public void keyAccessed(K key){
        //Does nth
    }
    @Override
    public void keyRemoved(K key){
        //Does nth
    }
    @Override
    public Optional<K> selectVictim(){
        return Optional.empty();
        //Does nth
    }
}
