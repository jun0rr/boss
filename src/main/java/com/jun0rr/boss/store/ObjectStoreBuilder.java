/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss.store;

import com.jun0rr.binj.BinContext;
import com.jun0rr.binj.BinType;
import com.jun0rr.binj.buffer.BufferAllocator;
import com.jun0rr.binj.buffer.FileNameSupplier;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.function.Supplier;

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


  private InvokeStrategy<ConstructFunction> constructStrategy;

  private InvokeStrategy<ExtractFunction> extractStrategy;

  private InvokeStrategy<InjectFunction> injectStrategy;

  private String volid;

  private int blockSize;

  private int bufferSize;

  private AllocatorType allocType;

  private Path storePath;

  public ObjectStoreBuilder() {}

  public InvokeStrategy<ConstructFunction> getConstructStrategy() {
    return constructStrategy;
  }

  public ObjectStoreBuilder setConstructStrategy(InvokeStrategy<ConstructFunction> constructStrategy) {
    this.constructStrategy = constructStrategy;
    return this;
  }

  public InvokeStrategy<ExtractFunction> getExtractStrategy() {
    return extractStrategy;
  }

  public ObjectStoreBuilder setExtractStrategy(InvokeStrategy<ExtractFunction> extractStrategy) {
    this.extractStrategy = extractStrategy;
    return this;
  }

  public InvokeStrategy<InjectFunction> getInjectStrategy() {
    return injectStrategy;
  }

  public ObjectStoreBuilder setInjectStrategy(InvokeStrategy<InjectFunction> injectStrategy) {
    this.injectStrategy = injectStrategy;
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
    return "Builder{" + "constructStrategy=" + constructStrategy + ", extractStrategy=" + extractStrategy + ", injectStrategy=" + injectStrategy + ", volumeId=" + volid + ", blockSize=" + blockSize + ", bufferSize=" + bufferSize + ", allocType=" + allocType + ", storePath=" + storePath + '}';
  }

  public ObjectStoreBuilder load(Properties p) {
    try {
      this.volid = p.getProperty(PROP_VOLUME_ID);
      InvokeStrategy<ConstructFunction> creator = new NoArgsConstructStrategy();
      String cct = p.getProperty(PROP_CONSTRUCT_STRATEGY);
      if(cct != null) {
        Class cctClass = Class.forName(cct);
        this.constructStrategy = creator.invokers(cctClass).stream().findFirst().get().create();
      }
      String ext = p.getProperty(PROP_EXTRACT_STRATEGY);
      if(ext != null) {
        Class extClass = Class.forName(ext);
        this.extractStrategy = creator.invokers(extClass).stream().findFirst().get().create();
      }
      String inj = p.getProperty(PROP_INJECT_STRATEGY);
      if(inj != null) {
        Class injClass = Class.forName(inj);
        this.injectStrategy = creator.invokers(injClass).stream().findFirst().get().create();
      }
      this.blockSize = Integer.parseInt(p.getProperty(PROP_BLOCK_SIZE));
      this.bufferSize = Integer.parseInt(p.getProperty(PROP_BUFFER_SIZE));
      this.allocType = AllocatorType.parse(p.getProperty(PROP_BUFFER_ALLOCATOR));
      String path = p.getProperty(PROP_STORE_PATH);
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
    if(this.volid == null) {
      throw new IllegalStateException("Volume ID is not defined");
    }
    BinContext ctx = BinContext.newContext();
    ctx.mapper().constructStrategy().add(constructStrategy);
    ctx.mapper().extractStrategy().add(extractStrategy);
    if(this.injectStrategy != null) {
      ctx.mapper().injectStrategy().add(injectStrategy);
    }
    BinType<IndexType> itype = BinType.of(IndexType.class);
    BinType<IndexValue> ivalue = BinType.of(IndexValue.class);
    BinType<DefaultIndex> indexType = BinType.of(DefaultIndex.class);
    ctx.codecs().put(itype, new ObjectCodec(ctx, itype));
    ctx.codecs().put(ivalue, new ObjectCodec(ctx, ivalue));
    ctx.codecs().put(indexType, new ObjectCodec(ctx, indexType));
    BufferAllocator valloc = BufferAllocator.heapAllocator(bufferSize);
    BufferAllocator ialloc = BufferAllocator.heapAllocator(bufferSize);
    switch(this.allocType) {
      case DIRECT:
        valloc = BufferAllocator.directAllocator(bufferSize);
        break;
      case MAPPED:
        if(this.storePath == null) {
          throw new IllegalStateException("Store path is not defined");
        }
        valloc = BufferAllocator.mappedFileAllocator(storePath, new FileNameSupplier(volid, "odb"), bufferSize, false);
        ialloc = BufferAllocator.mappedFileAllocator(storePath, new FileNameSupplier(volid, "idx"), bufferSize, false);
        break;
    }
    Supplier<String> vname = new FileNameSupplier(volid, "odb");
    List<ByteBuffer> vbufs = new LinkedList<>();
    while(Files.exists(storePath.resolve(vname.get()))) {
      vbufs.add(valloc.alloc());
    }
    Supplier<String> iname = new FileNameSupplier(volid, "idx");
    List<ByteBuffer> ibufs = new LinkedList<>();
    while(Files.exists(storePath.resolve(iname.get()))) {
      ibufs.add(ialloc.alloc());
    }
    Volume vol = new DefaultVolume(volid, blockSize, vbufs, valloc);
    Volume idx = new DefaultVolume(volid, blockSize, ibufs, ialloc);
    return new DefaultObjectStore(vol, idx, ctx);
  }
    
}
