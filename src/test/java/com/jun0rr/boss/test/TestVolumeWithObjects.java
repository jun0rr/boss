/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss.test;

import com.jun0rr.binj.BinContext;
import com.jun0rr.binj.BinType;
import com.jun0rr.binj.buffer.BufferAllocator;
import com.jun0rr.binj.buffer.FileNameSupplier;
import com.jun0rr.binj.codec.ObjectCodec;
import com.jun0rr.binj.mapping.AnnotationExtractStrategy;
import com.jun0rr.binj.mapping.DefaultConstructStrategy;
import com.jun0rr.boss.Block;
import com.jun0rr.boss.Volume;
import com.jun0rr.boss.volume.DefaultVolume;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 *
 * @author F6036477
 */
public class TestVolumeWithObjects {
  
  @Test
  public void test() {
    try {
      BinContext ctx = BinContext.newContext();
      ctx.mapper().constructStrategy().add(new DefaultConstructStrategy());
      ctx.mapper().extractStrategy().add(new AnnotationExtractStrategy());
      BinType ptype = BinType.of(Person.class);
      BinType atype = BinType.of(Address.class);
      ctx.codecs().put(ptype, new ObjectCodec(ctx, ptype));
      ctx.codecs().put(atype, new ObjectCodec(ctx, atype));
      Volume vol = volume();
      Volume idx = indexVolume();
      System.out.println(vol);
      List<Person> ps = new LinkedList<>();
      IntStream.range(0, 10).mapToObj(i->new Person(
          "Hello" + i, "World" + i, LocalDate.of(1980, i + 1, i + 10), 
          new Address("Street" + i, "City" + i, i + 100), new long[]{299L + i})
      ).forEach(ps::add);
      System.out.println(ps);
      List<Integer> is = new LinkedList<>();
      if(vol.isLoaded()) {
        is.addAll(ctx.read(idx.get(idx.blockSize()).buffer()));
      }
      else {
        for(Person p : ps) {
          Block b = vol.allocate();
          ctx.write(b.buffer(), p);
          is.add(b.index());
        }
        ctx.write(idx.allocate().buffer(), is);
      }
      System.out.println(is);
      vol.release(is.remove(5));
      ps.remove(5);
      for(int i = 0; i < ps.size(); i++) {
        Block b = vol.get(is.get(i));
        assertEquals(ps.get(i), ctx.read(b.buffer()));
      }
      vol.close();
      idx.close();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  
  private Volume volume() {
    Path path = Paths.get("./");
    BufferAllocator alloc = BufferAllocator.mappedFileAllocator(path, new FileNameSupplier(getClass().getSimpleName(), "vol"), 256, false);
    Supplier<String> fname = new FileNameSupplier(getClass().getSimpleName(), "vol");
    List<ByteBuffer> bufs = new LinkedList<>();
    while(Files.exists(path.resolve(fname.get()))) {
      bufs.add(alloc.alloc());
    }
    return new DefaultVolume(getClass().getCanonicalName(), 128, bufs, alloc);
  }
  
  private Volume indexVolume() {
    Path path = Paths.get("./");
    BufferAllocator alloc = BufferAllocator.mappedFileAllocator(path, new FileNameSupplier(getClass().getSimpleName(), "idx"), 256, false);
    Supplier<String> fname = new FileNameSupplier(getClass().getSimpleName(), "idx");
    List<ByteBuffer> bufs = new LinkedList<>();
    while(Files.exists(path.resolve(fname.get()))) {
      bufs.add(alloc.alloc());
    }
    return new DefaultVolume(getClass().getCanonicalName(), 128, bufs, alloc);
  }
  
}
