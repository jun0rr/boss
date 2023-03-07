/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss.test;

import com.jun0rr.binj.buffer.MappedBufferAllocator;
import com.jun0rr.binj.buffer.PathSupplier;
import com.jun0rr.boss.Block;
import com.jun0rr.boss.Volume;
import com.jun0rr.boss.volume.DefaultVolume;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * @author F6036477
 */
public class TestMultiThreadVolume {
  
  //@Test
  public void test() {
    try {
      MappedBufferAllocator ma = new MappedBufferAllocator(PathSupplier.of(Paths.get("./"), "TestMultiThreadVolume", "bin"), 64);
      Volume vol = new DefaultVolume("TestMultiThreadVolume", 32, ma.readBuffers(), ma);
      System.out.println(vol);
      Queue<Integer> offsets = new ConcurrentLinkedQueue<>();
      List<Thread> ls = IntStream.range(0, 20)
          .mapToObj(i->VolumeTask.task(vol, write(offsets)))
          .map(VolumeTask::start)
          .collect(Collectors.toList());
      List<Thread> ls2 = offsets.stream()
          .peek(i->offsets.remove(i))
          .map(i->VolumeTask.task(vol, read(i)))
          .map(VolumeTask::start)
          .collect(Collectors.toList());
      ls.forEach(this::join);
      while(!offsets.isEmpty()) {
        ls = offsets.stream()
            .peek(i->offsets.remove(i))
            .map(i->VolumeTask.task(vol, read(i)))
            .map(VolumeTask::start)
            .collect(Collectors.toList());
      }
      ls.forEach(this::join);
      ls2.forEach(this::join);
      System.out.println(offsets);
      vol.close();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  
  private Consumer<Volume> write(Queue<Integer> offsets) {
    return v->{
      Block b = v.allocate();
      IntStream.range(10, 30).forEach(b.buffer()::putInt);
      offsets.offer(b.offset());
      b.buffer().flip();
      //System.out.printf("put(%d): %s, %s%n", b.index(), IntStream.range(10, 30).mapToObj(j->b.buffer().getInt()).collect(Collectors.toList()), b);
    };
  }
  
  private Consumer<Volume> read(int idx) {
    return v->{
      Block b = v.get(idx);
      System.out.printf("get(%d, %s): %s%n", b.offset(), Thread.currentThread(), b);
      IntStream.range(10, 30).forEach(j->Assertions.assertEquals(j, b.buffer().getInt()));
      v.release(b);
      //System.out.printf("%s%n", IntStream.range(10, 30).mapToObj(j->b.buffer().getInt()).collect(Collectors.toList()));
    };
  }
  
  private void join(Thread t) {
    try {
      t.join();
    }
    catch(InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
  
  public static class VolumeTask implements Runnable {
    
    private final Volume vol;
    
    private final Consumer<Volume> task;
    
    public VolumeTask(Volume v, Consumer<Volume> c) {
      this.vol = Objects.requireNonNull(v);
      this.task = Objects.requireNonNull(c);
    }
    
    @Override
    public void run() {
      task.accept(vol);
    }
    
    public Thread start() {
      Thread t = new Thread(this);
      t.start();
      return t;
    }
    
    public static VolumeTask task(Volume v, Consumer<Volume> c) {
      return new VolumeTask(v, c);
    }
    
  }
  
}
