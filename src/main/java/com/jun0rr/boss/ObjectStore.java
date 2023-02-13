/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.jun0rr.boss;

import com.jun0rr.boss.store.ObjectStoreException;
import com.jun0rr.boss.store.Stored;
import com.jun0rr.boss.store.DefaultObjectStore;
import com.jun0rr.boss.volume.DefaultVolume;
import com.jun0rr.boss.volume.FirstBlockPersistStrategy;
import com.jun0rr.boss.volume.NoPersistStrategy;
import com.jun0rr.jbom.BinContext;
import com.jun0rr.jbom.buffer.BufferAllocator;
import com.jun0rr.jbom.buffer.FileNameSupplier;
import com.jun0rr.jbom.mapping.ConstructStrategy;
import com.jun0rr.jbom.mapping.ExtractStrategy;
import com.jun0rr.jbom.mapping.InjectStrategy;
import com.jun0rr.jbom.mapping.NoArgsConstructStrategy;
import java.io.Closeable;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
 *
 * @author F6036477
 */
public interface ObjectStore extends Closeable {
  
  public <T> Stored<T> store(T o);
  
  public <T> Optional<Stored<T>> get(long id);
  
  public <T> Stored<T> update(long id, T o);
  
  public <T> Stored<T> update(long id, UnaryOperator<T> update);
  
  public <T> Stream<Stored<T>> find(Class<T> c, Predicate<T> p);
  
  public <T,V> Stream<Stored<T>> find(Class<T> c, String name, Predicate<V> p);
  
  public <T> Optional<Stored<T>> delete(long id);
  
  public <T> Stream<Stored<T>> delete(Class<T> c, Predicate<T> p);
  
  public <T,R> void createIndex(Class<T> c, String name, Function<T,R> fn);
  
  public IndexStore indexStore();
  
  @Override public void close();
  
  public static Builder builder() {
    return new Builder();
  }
  
  
  
  public static class Builder {
    
    public static final String KEY_CONSTRUCT_STRATEGY = "boss.mapper.constructStrategy";
    
    public static final String KEY_EXTRACT_STRATEGY = "boss.mapper.extractStrategy";
    
    public static final String KEY_INJECT_STRATEGY = "boss.mapper.injectStrategy";
    
    public static final String KEY_VOLUME_ID = "boss.volume.id";
    
    public static final String KEY_BLOCK_SIZE = "boss.volume.blockSize";
    
    public static final String KEY_BUFFER_SIZE = "boss.volume.bufferSize";
    
    public static final String KEY_BUFFER_ALLOCATOR = "boss.volume.bufferAllocator";
    
    public static final String KEY_STORE_PATH = "boss.volume.storePath";
    
    public static enum AllocatorType {
      HEAP, DIRECT, MAPPED;
      
      public static AllocatorType parse(String s) {
        if(HEAP.name().equalsIgnoreCase(s)) {
          return HEAP;
        }
        else if(DIRECT.name().equalsIgnoreCase(s)) {
          return DIRECT;
        }
        else if(MAPPED.name().equalsIgnoreCase(s)) {
          return MAPPED;
        }
        else {
          throw new IllegalArgumentException("Bad AllocatorType: " + s);
        }
      }
    }
    
    
    private ConstructStrategy constructStrategy;
    
    private ExtractStrategy extractStrategy;
    
    private InjectStrategy injectStrategy;
    
    private String volumeId;
    
    private int blockSize;
    
    private int bufferSize;
    
    private AllocatorType allocType;
    
    private Path storePath;
    
    public Builder() {}

    public ConstructStrategy getConstructStrategy() {
      return constructStrategy;
    }

    public Builder setConstructStrategy(ConstructStrategy constructStrategy) {
      this.constructStrategy = constructStrategy;
      return this;
    }

    public ExtractStrategy getExtractStrategy() {
      return extractStrategy;
    }

    public Builder setExtractStrategy(ExtractStrategy extractStrategy) {
      this.extractStrategy = extractStrategy;
      return this;
    }

    public InjectStrategy getInjectStrategy() {
      return injectStrategy;
    }

    public Builder setInjectStrategy(InjectStrategy injectStrategy) {
      this.injectStrategy = injectStrategy;
      return this;
    }

    public String getVolumeId() {
      return volumeId;
    }

    public Builder setVolumeId(String volumeId) {
      this.volumeId = volumeId;
      return this;
    }

    public int getBlockSize() {
      return blockSize;
    }

    public Builder setBlockSize(int blockSize) {
      this.blockSize = blockSize;
      return this;
    }

    public int getBufferSize() {
      return bufferSize;
    }

    public Builder setBufferSize(int bufferSize) {
      this.bufferSize = bufferSize;
      return this;
    }

    public AllocatorType getBufferAllocatorType() {
      return allocType;
    }

    public Builder setBufferAllocatorType(AllocatorType allocType) {
      this.allocType = allocType;
      return this;
    }

    public Path getStorePath() {
      return storePath;
    }

    public Builder setStorePath(Path storePath) {
      this.storePath = storePath;
      return this;
    }

    @Override
    public String toString() {
      return "Builder{" + "constructStrategy=" + constructStrategy + ", extractStrategy=" + extractStrategy + ", injectStrategy=" + injectStrategy + ", volumeId=" + volumeId + ", blockSize=" + blockSize + ", bufferSize=" + bufferSize + ", allocType=" + allocType + ", storePath=" + storePath + '}';
    }
    
    public Builder load(Properties p) {
      try {
        this.volumeId = p.getProperty(KEY_VOLUME_ID);
        ConstructStrategy creator = new NoArgsConstructStrategy();
        String cct = p.getProperty(KEY_CONSTRUCT_STRATEGY);
        if(cct != null) {
          Class cctClass = Class.forName(cct);
          this.constructStrategy = creator.constructors(cctClass).stream().findFirst().get().create();
        }
        String ext = p.getProperty(KEY_EXTRACT_STRATEGY);
        if(ext != null) {
          Class extClass = Class.forName(ext);
          this.extractStrategy = creator.constructors(extClass).stream().findFirst().get().create();
        }
        String inj = p.getProperty(KEY_INJECT_STRATEGY);
        if(inj != null) {
          Class injClass = Class.forName(inj);
          this.injectStrategy = creator.constructors(injClass).stream().findFirst().get().create();
        }
        this.blockSize = Integer.parseInt(p.getProperty(KEY_BLOCK_SIZE));
        this.bufferSize = Integer.parseInt(p.getProperty(KEY_BUFFER_SIZE));
        this.allocType = AllocatorType.parse(p.getProperty(KEY_BUFFER_ALLOCATOR));
        String path = p.getProperty(KEY_STORE_PATH);
        if(path != null) {
          this.storePath = Paths.get(path).toAbsolutePath();
          //System.out.println("* ObjectStore.Builder.load(): storePath=" + storePath);
        }
      }
      catch(Exception e) {
        throw new ObjectStoreException(e);
      }
      return this;
    }
    
    public ObjectStore build() {
      if(this.constructStrategy == null) {
        throw new IllegalStateException("ConstructStrategy is not defined");
      }
      if(this.extractStrategy == null) {
        throw new IllegalStateException("ExtractStrategy is not defined");
      }
      if(this.allocType == null) {
        throw new IllegalStateException("BufferAllocator is not defined");
      }
      if(this.blockSize == 0) {
        throw new IllegalStateException("Block size is not defined");
      }
      if(this.bufferSize == 0) {
        throw new IllegalStateException("Buffer size is not defined");
      }
      if(this.volumeId == null) {
        throw new IllegalStateException("Volume ID is not defined");
      }
      BinContext ctx = BinContext.newContext();
      ctx.mapper().constructStrategy().add(constructStrategy);
      ctx.mapper().extractStrategy().add(extractStrategy);
      if(this.injectStrategy != null) {
        ctx.mapper().injectStrategy().add(injectStrategy);
      }
      MetaPersistStrategy mps = new NoPersistStrategy();
      BufferAllocator malloc = BufferAllocator.heapAllocator(bufferSize);
      switch(this.allocType) {
        case DIRECT:
          malloc = BufferAllocator.directAllocator(bufferSize);
          break;
        case MAPPED:
          if(this.storePath == null) {
            throw new IllegalStateException("Store path is not defined");
          }
          malloc = BufferAllocator.mappedFileAllocator(storePath, new FileNameSupplier(volumeId, "db"), bufferSize, true);
          mps = new FirstBlockPersistStrategy(ctx);
          break;
      }
      Supplier<String> fname = new FileNameSupplier(volumeId, "db");
      List<ByteBuffer> bufs = new LinkedList<>();
      while(Files.exists(storePath.resolve(fname.get()))) {
        bufs.add(malloc.alloc());
      }
      Volume vol = new DefaultVolume(volumeId, blockSize, bufs, malloc, mps);
      return new DefaultObjectStore((bufs.isEmpty() ? vol : vol.loadNewVolume()), ctx);
    }
    
  }
  
}
