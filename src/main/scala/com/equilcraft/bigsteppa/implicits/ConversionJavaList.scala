package com.equilcraft.bigsteppa.implicits

object ConversionJavaList {
  implicit class JavaListForeach[T](val list: java.util.List[T]) extends AnyVal {
    @inline def foreach(fn: T => Unit): Unit = {
      var i = 0
      val size = list.size()
      while (i < size) {
        fn(list.get(i))
        i += 1
      }
    }
  }
}
