/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss.store;

import com.jun0rr.binj.*;
import com.jun0rr.binj.buffer.BufferAllocator;
import com.jun0rr.binj.buffer.MappedBufferAllocator;
import com.jun0rr.binj.buffer.PathSupplier;
import com.jun0rr.binj.codec.ObjectCodec;
import com.jun0rr.binj.mapping.ConstructFunction;
import com.jun0rr.binj.mapping.ExtractFunction;
import com.jun0rr.binj.mapping.InjectFunction;
import com.jun0rr.binj.mapping.InvokeStrategy;
import com.jun0rr.binj.mapping.NoArgsConstructStrategy;
import com.jun0rr.boss.ObjectStore;
import com.jun0rr.boss.Volume;
import com.jun0rr.boss.volume.DefaultVolume;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author F6036477
 */
public class ObjectStoreBuilder {

  public static final String PROP_CONSTRUCT_STRATEGY = "boss.mapper.constructStrategy";

  public static final String PROP_EXTRACT_STRATEGY = "boss.mapper.extractStrategy";

  public static final String PROP_INJECT_STRATEGY = "boss.mapper.injectStrategy";

  public static final String PROP_VOLUME_ID = "boss.volume.id";

  public static final String PROP_BLOCK_SIZE = "boss.volume.blockSize";

  public static final String PROP_BUFFER_SIZE = "boss.volume.bufferSize";

  public static final String PROP_BUFFER_ALLOCATOR = "boss.volume.bufferAllocator";

  public static final String PROP_STORE_PATH = "boss.volume.storePath";

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


  private List<InvokeStrategy<ConstructFunction>> constructors;

  private List<InvokeStrategy<ExtractFunction>> extractors;

  private List<InvokeStrategy<InjectFunction>> injectors;

  private String volid;

  private int blockSize;

  private int bufferSize;

  private AllocatorType allocType;

  private Path storePath;

  public ObjectStoreBuilder() {
    this.constructors = new LinkedList<>();
    this.extractors = new LinkedList<>();
    this.injectors = new LinkedList<>();
  }

  public List<InvokeStrategy<ConstructFunction>> getConstructStrategies() {
    return constructors;
  }

  public ObjectStoreBuilder addConstructStrategy(InvokeStrategy<ConstructFunction> constructStrategy) {
    this.constructors.add(constructStrategy);
    return this;
  }
  
  public ObjectStoreBuilder setConstructStrategies(List<InvokeStrategy<ConstructFunction>> constructors) {
    this.constructors = constructors;
    return this;
  }
  
  public List<InvokeStrategy<ExtractFunction>> getExtractStrategies() {
    return extractors;
  }

  public ObjectStoreBuilder setExtractStrategy(InvokeStrategy<ExtractFunction> extractStrategy) {
    this.extractors.add(extractStrategy);
    return this;
  }

  public ObjectStoreBuilder setExtractStrategies(List<InvokeStrategy<ExtractFunction>> extractors) {
    this.extractors = extractors;
    return this;
  }

  public List<InvokeStrategy<InjectFunction>> getInjectStrategies() {
    return injectors;
  }

  public ObjectStoreBuilder addInjectStrategy(InvokeStrategy<InjectFunction> injectStrategy) {
    this.injectors.add(injectStrategy);
    return this;
  }

  public ObjectStoreBuilder setInjectStrategies(List<InvokeStrategy<InjectFunction>> injectors) {
    this.injectors = injectors;
    return this;
  }

  public String getVolumeId() {
    return volid;
  }

  public ObjectStoreBuilder setVolumeId(String volumeId) {
    this.volid = volumeId;
    return this;
  }

  public int getBlockSize() {
    return blockSize;
  }

  public ObjectStoreBuilder setBlockSize(int blockSize) {
    this.blockSize = blockSize;
    return this;
  }

  public int getBufferSize() {
    return bufferSize;
  }

  public ObjectStoreBuilder setBufferSize(int bufferSize) {
    this.bufferSize = bufferSize;
    return this;
  }

  public AllocatorType getBufferAllocatorType() {
    return allocType;
  }

  public ObjectStoreBuilder setBufferAllocatorType(AllocatorType allocType) {
    this.allocType = allocType;
    return this;
  }

  public Path getStorePath() {
    return storePath;
  }

  public ObjectStoreBuilder setStorePath(Path storePath) {
    this.storePath = storePath;
    return this;
  }

  @Override
  public String toString() {
    return "Builder{" + "constructors=" + constructors + ", extractors=" + extractors + ", injectors=" + injectors + ", volumeId=" + volid + ", blockSize=" + blockSize + ", bufferSize=" + bufferSize + ", allocType=" + allocType + ", storePath=" + storePath + '}';
  }

  public ObjectStoreBuilder set(Properties p) {
    try {
      this.volid = p.getProperty(PROP_VOLUME_ID);
      InvokeStrategy<ConstructFunction> creator = new NoArgsConstructStrategy();
      String cct = p.getProperty(PROP_CONSTRUCT_STRATEGY);
      if(cct != null) {
        List.of(cct.split(",")).stream()
            .map(this::forName)
            .map(c->creator.invokers(c).stream().findFirst().get().create())
            .map(o->(InvokeStrategy<ConstructFunction>)o)
            .forEach(constructors::add);
      }
      String ext = p.getProperty(PROP_EXTRACT_STRATEGY);
      if(ext != null) {
        List.of(ext.split(",")).stream()
            .map(this::forName)
            .map(c->creator.invokers(c).stream().findFirst().get().create())
            .map(o->(InvokeStrategy<ExtractFunction>)o)
            .forEach(extractors::add);
      }
      String inj = p.getProperty(PROP_INJECT_STRATEGY);
      if(inj != null) {
        List.of(inj.split(",")).stream()
            .map(this::forName)
            .map(c->creator.invokers(c).stream().findFirst().get().create())
            .map(o->(InvokeStrategy<InjectFunction>)o)
            .forEach(injectors::add);
      }
      this.blockSize = Integer.parseInt(p.getProperty(PROP_BLOCK_SIZE));
      this.bufferSize = Integer.parseInt(p.getProperty(PROP_BUFFER_SIZE));
      this.allocType = AllocatorType.parse(p.getProperty(PROP_BUFFER_ALLOCATOR));
      String path = p.getProperty(PROP_STORE_PATH);
      if(path != null) {
        this.storePath = Paths.get(path).toAbsolutePath();
      }
    }
    catch(Exception e) {
      throw new ObjectStoreException(e);
    }
    return this;
  }
  
  private Class forName(String s) {
    try {
      return Class.forName(s);
    }
    catch(ClassNotFoundException e) {
      throw new ObjectStoreException(e);
    }
  }

  public ObjectStore build() {
    if(this.constructors == null) {
      throw new IllegalStateException("ConstructStrategy is not defined");
    }
    if(this.extractors == null) {
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
    if(this.volid == null) {
      throw new IllegalStateException("Volume ID is not defined");
    }
    return new DefaultObjectStore(buildVolume(), buildContext());
  }
  
  private BinContext buildContext() {
    BinContext ctx = BinContext.newContext();
    constructors.forEach(ctx.mapper().constructStrategies()::add);
    extractors.forEach(ctx.mapper().extractStrategies()::add);
    if(!this.injectors.isEmpty()) {
      injectors.forEach(ctx.mapper().injectStrategies()::add);
    }
    System.out.printf("* Constructors: %s%n", constructors);
    System.out.printf("* Extractors..: %s%n", extractors);
    System.out.printf("* Injectors...: %s%n", injectors);
    BinType<IndexType> itype = BinType.of(IndexType.class);
    BinType<IndexValue> ivalue = BinType.of(IndexValue.class);
    BinType<DefaultIndex> indexType = BinType.of(DefaultIndex.class);
    ctx.codecs().put(itype, new ObjectCodec(ctx, itype));
    ctx.codecs().put(ivalue, new ObjectCodec(ctx, ivalue));
    ctx.codecs().put(indexType, new ObjectCodec(ctx, indexType));
    return ctx;
  }
  
  private Volume buildVolume() {
    List<ByteBuffer> bufs = Collections.EMPTY_LIST;
    BufferAllocator alloc = BufferAllocator.heapAllocator(bufferSize);
    switch(this.allocType) {
      case DIRECT:
        alloc = BufferAllocator.directAllocator(bufferSize);
        break;
      case MAPPED:
        if(this.storePath == null) {
          throw new IllegalStateException("Store path is not defined");
        }
        MappedBufferAllocator ma = new MappedBufferAllocator(
            PathSupplier.of(storePath, volid, "odb"), bufferSize, false
        );
        alloc = ma;
        bufs = ma.readBuffers();
        break;
    }
    return new DefaultVolume(volid, blockSize, bufs, alloc);
  }
  
}
