package com.demo.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;

public class ForkJoinPoolDemo {
   public static void main(String[] args) {
      ForkJoinPool forkJoinPool = new ForkJoinPool(4);
//      ForkJoinMyRecursiveAction myRecursiveAction = new ForkJoinMyRecursiveAction(256);
//      forkJoinPool.invoke(myRecursiveAction);

      System.err.println("-------------------");
      ForkJoinMyRecursiveTask myRecursiveTask = new ForkJoinMyRecursiveTask(128);
      long mergedResult = forkJoinPool.invoke(myRecursiveTask);
      System.out.println("mergedResult = " + mergedResult);
   }
}

class ForkJoinMyRecursiveAction extends RecursiveAction {
   private static final long serialVersionUID = 1L;
   private long workLoad = 0;

   public ForkJoinMyRecursiveAction(long workLoad) {
      this.workLoad = workLoad;
   }

   @Override
   protected void compute() {
      // if work is above threshold, break tasks up into smaller tasks
      if(this.workLoad > 16) {
         System.out.println("Splitting workLoad : " + this.workLoad);
         List<ForkJoinMyRecursiveAction> subtasks = new ArrayList<ForkJoinMyRecursiveAction>();
         subtasks.addAll(createSubtasks());

         for(RecursiveAction subtask : subtasks) {
            subtask.fork();
         }
      }
      else {
         System.out.println("Doing workLoad myself: " + this.workLoad);
      }
   }

   private List<ForkJoinMyRecursiveAction> createSubtasks() {
      List<ForkJoinMyRecursiveAction> subtasks = new ArrayList<ForkJoinMyRecursiveAction>();
      ForkJoinMyRecursiveAction subtask1 = new ForkJoinMyRecursiveAction(this.workLoad / 2);
      ForkJoinMyRecursiveAction subtask2 = new ForkJoinMyRecursiveAction(this.workLoad / 2);
      subtasks.add(subtask1);
      subtasks.add(subtask2);

      return subtasks;
   }
}

class ForkJoinMyRecursiveTask extends RecursiveTask<Long> {
   private static final long serialVersionUID = 1L;
   private long workLoad = 0;

   public ForkJoinMyRecursiveTask(long workLoad) {
      this.workLoad = workLoad;
   }

   protected Long compute() {
      // if work is above threshold, break tasks up into smaller tasks
      if(this.workLoad > 16) {
         System.out.println("Splitting workLoad : " + this.workLoad);

         List<ForkJoinMyRecursiveTask> subtasks = new ArrayList<ForkJoinMyRecursiveTask>();
         subtasks.addAll(createSubtasks());

         for(ForkJoinMyRecursiveTask subtask : subtasks) {
            subtask.fork();
         }

         long result = 0;

         for(ForkJoinMyRecursiveTask subtask : subtasks) {
            result += subtask.join();
         }

         return result;
      }
      else {
         System.out.println("Doing workLoad myself: " + this.workLoad);
         return workLoad * 3;
      }
   }

   private List<ForkJoinMyRecursiveTask> createSubtasks() {
      List<ForkJoinMyRecursiveTask> subtasks = new ArrayList<ForkJoinMyRecursiveTask>();
      ForkJoinMyRecursiveTask subtask1 = new ForkJoinMyRecursiveTask(this.workLoad / 2);
      ForkJoinMyRecursiveTask subtask2 = new ForkJoinMyRecursiveTask(this.workLoad / 2);
      subtasks.add(subtask1);
      subtasks.add(subtask2);

      return subtasks;
   }
}