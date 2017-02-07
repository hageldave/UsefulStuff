/* AUTOMATICALLY GENERATED FROM  src\main\java\array\ProtoSlice.java */
package array;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Slice<T> implements Iterable<Slice.ArrayAccessor<T>>{
	
	final ArrayAccessor<T> aa;
	final int size;
	final int beginIdx;
	
	private Slice(ArrayAccessor<T> accessor, int beginIdx, int size) {
		this.aa = accessor;
		this.size = size;
		this.beginIdx = beginIdx;
	}
	
	@Override
	public Iterator<ArrayAccessor<T>> iterator() {
		return new AccessorIterator<T>(beginIdx, size, aa);
	}
	
	@Override
	public Spliterator<ArrayAccessor<T>> spliterator() {
		return new AccessorSpliterator<T>(beginIdx, size-1, estimateReasonableSplitSize(size), aa);
	}
	
	public Stream<ArrayAccessor<T>> stream(boolean parallel) {
		return StreamSupport.stream(spliterator(), parallel);
	}
	
	public Stream<ArrayAccessor<T>> stream() {
		return stream(false);
	}
	
	public Stream<ArrayAccessor<T>> parallelStream() {
		return stream(true);
	}
	
	@Override
	public void forEach(Consumer<? super ArrayAccessor<T>> action) {
		Iterable.super.forEach(action);
	}
	
	public void forEachParallel(Consumer<? super ArrayAccessor<T>> action) {
		parallelStream().forEach(action);
	}
	
	public Slice<T> copy() {
		return new Slice<T>(aa.copy(),beginIdx,size);
	}
	
	public T getAt(int i){
		return aa.get(i);
	}
	
	public void setAt(int i, T e){
		aa.set(i, e);
	}
	
	public int length() {
		return size;
	}
	
	public int size() {
		return size;
	}
	
	public int getBeginIdx() {
		return beginIdx;
	}
	
	///////////////////////////////
	// Constructors
	///////////////////////////////
	
	static <T> Slice<T> get(T[] array, int beginIdx, int length){
		return new Slice<T>(new GenericAccessor<T>(array), beginIdx, length);
	}
	
	static <T> Slice<T> get(T[] array){
		return get(array, 0, array.length);
	}

	static  Slice<Integer> get(int[] array, int beginIdx, int length){
		return new Slice<Integer>(new IntAccessor(array), beginIdx, length);
	}
	
	static  Slice<Integer> get(int[] array){
		return get(array, 0, array.length);
	}

	static  Slice<Long> get(long[] array, int beginIdx, int length){
		return new Slice<Long>(new LongAccessor(array), beginIdx, length);
	}
	
	static  Slice<Long> get(long[] array){
		return get(array, 0, array.length);
	}

	static  Slice<Float> get(float[] array, int beginIdx, int length){
		return new Slice<Float>(new FloatAccessor(array), beginIdx, length);
	}
	
	static  Slice<Float> get(float[] array){
		return get(array, 0, array.length);
	}

	static  Slice<Double> get(double[] array, int beginIdx, int length){
		return new Slice<Double>(new DoubleAccessor(array), beginIdx, length);
	}
	
	static  Slice<Double> get(double[] array){
		return get(array, 0, array.length);
	}

	static  Slice<Short> get(short[] array, int beginIdx, int length){
		return new Slice<Short>(new ShortAccessor(array), beginIdx, length);
	}
	
	static  Slice<Short> get(short[] array){
		return get(array, 0, array.length);
	}

	static  Slice<Character> get(char[] array, int beginIdx, int length){
		return new Slice<Character>(new CharAccessor(array), beginIdx, length);
	}
	
	static  Slice<Character> get(char[] array){
		return get(array, 0, array.length);
	}

	static  Slice<Byte> get(byte[] array, int beginIdx, int length){
		return new Slice<Byte>(new ByteAccessor(array), beginIdx, length);
	}
	
	static  Slice<Byte> get(byte[] array){
		return get(array, 0, array.length);
	}

	
	///////////////////////////////
	// Static Streaming
	///////////////////////////////
	
	static int estimateReasonableSplitSize(int length) {
		final int tasksPerProcessor = 16;
		final int concurrentLimit = Runtime.getRuntime().availableProcessors()*tasksPerProcessor;
		final int minReasonable = 512;
		return Math.max(minReasonable, Integer.highestOneBit(length/concurrentLimit));
	}
	
	public static <T> Stream<ArrayAccessor<T>> stream(T[] array, boolean parallel, int beginIndex, int length, int minSplitSize){
		return StreamSupport.stream(new AccessorSpliterator<T>(beginIndex, beginIndex+length-1, minSplitSize, new GenericAccessor<T>(array)), parallel);
	}
	
	public static <T> Stream<ArrayAccessor<T>> stream(T[] array, boolean parallel, int beginIndex, int length){
		return stream(array, parallel, beginIndex, length, estimateReasonableSplitSize(length));
	}
	
	public static <T> Stream<ArrayAccessor<T>> stream(T[] array, boolean parallel){
		return stream(array, parallel, 0, array.length);
	}

	public static  Stream<ArrayAccessor<Integer>> stream(int[] array, boolean parallel, int beginIndex, int length, int minSplitSize){
		return StreamSupport.stream(new AccessorSpliterator<Integer>(beginIndex, beginIndex+length-1, minSplitSize, new IntAccessor(array)), parallel);
	}
	
	public static  Stream<ArrayAccessor<Integer>> stream(int[] array, boolean parallel, int beginIndex, int length){
		return stream(array, parallel, beginIndex, length, estimateReasonableSplitSize(length));
	}
	
	public static  Stream<ArrayAccessor<Integer>> stream(int[] array, boolean parallel){
		return stream(array, parallel, 0, array.length);
	}

	public static  Stream<ArrayAccessor<Long>> stream(long[] array, boolean parallel, int beginIndex, int length, int minSplitSize){
		return StreamSupport.stream(new AccessorSpliterator<Long>(beginIndex, beginIndex+length-1, minSplitSize, new LongAccessor(array)), parallel);
	}
	
	public static  Stream<ArrayAccessor<Long>> stream(long[] array, boolean parallel, int beginIndex, int length){
		return stream(array, parallel, beginIndex, length, estimateReasonableSplitSize(length));
	}
	
	public static  Stream<ArrayAccessor<Long>> stream(long[] array, boolean parallel){
		return stream(array, parallel, 0, array.length);
	}

	public static  Stream<ArrayAccessor<Float>> stream(float[] array, boolean parallel, int beginIndex, int length, int minSplitSize){
		return StreamSupport.stream(new AccessorSpliterator<Float>(beginIndex, beginIndex+length-1, minSplitSize, new FloatAccessor(array)), parallel);
	}
	
	public static  Stream<ArrayAccessor<Float>> stream(float[] array, boolean parallel, int beginIndex, int length){
		return stream(array, parallel, beginIndex, length, estimateReasonableSplitSize(length));
	}
	
	public static  Stream<ArrayAccessor<Float>> stream(float[] array, boolean parallel){
		return stream(array, parallel, 0, array.length);
	}

	public static  Stream<ArrayAccessor<Double>> stream(double[] array, boolean parallel, int beginIndex, int length, int minSplitSize){
		return StreamSupport.stream(new AccessorSpliterator<Double>(beginIndex, beginIndex+length-1, minSplitSize, new DoubleAccessor(array)), parallel);
	}
	
	public static  Stream<ArrayAccessor<Double>> stream(double[] array, boolean parallel, int beginIndex, int length){
		return stream(array, parallel, beginIndex, length, estimateReasonableSplitSize(length));
	}
	
	public static  Stream<ArrayAccessor<Double>> stream(double[] array, boolean parallel){
		return stream(array, parallel, 0, array.length);
	}

	public static  Stream<ArrayAccessor<Short>> stream(short[] array, boolean parallel, int beginIndex, int length, int minSplitSize){
		return StreamSupport.stream(new AccessorSpliterator<Short>(beginIndex, beginIndex+length-1, minSplitSize, new ShortAccessor(array)), parallel);
	}
	
	public static  Stream<ArrayAccessor<Short>> stream(short[] array, boolean parallel, int beginIndex, int length){
		return stream(array, parallel, beginIndex, length, estimateReasonableSplitSize(length));
	}
	
	public static  Stream<ArrayAccessor<Short>> stream(short[] array, boolean parallel){
		return stream(array, parallel, 0, array.length);
	}

	public static  Stream<ArrayAccessor<Character>> stream(char[] array, boolean parallel, int beginIndex, int length, int minSplitSize){
		return StreamSupport.stream(new AccessorSpliterator<Character>(beginIndex, beginIndex+length-1, minSplitSize, new CharAccessor(array)), parallel);
	}
	
	public static  Stream<ArrayAccessor<Character>> stream(char[] array, boolean parallel, int beginIndex, int length){
		return stream(array, parallel, beginIndex, length, estimateReasonableSplitSize(length));
	}
	
	public static  Stream<ArrayAccessor<Character>> stream(char[] array, boolean parallel){
		return stream(array, parallel, 0, array.length);
	}

	public static  Stream<ArrayAccessor<Byte>> stream(byte[] array, boolean parallel, int beginIndex, int length, int minSplitSize){
		return StreamSupport.stream(new AccessorSpliterator<Byte>(beginIndex, beginIndex+length-1, minSplitSize, new ByteAccessor(array)), parallel);
	}
	
	public static  Stream<ArrayAccessor<Byte>> stream(byte[] array, boolean parallel, int beginIndex, int length){
		return stream(array, parallel, beginIndex, length, estimateReasonableSplitSize(length));
	}
	
	public static  Stream<ArrayAccessor<Byte>> stream(byte[] array, boolean parallel){
		return stream(array, parallel, 0, array.length);
	}

	
	///////////////////////////////
	// Accessors
	///////////////////////////////
	
	public static abstract class ArrayAccessor<T> implements Cloneable {
		protected int index;
		
		public abstract T get();
		
		public abstract void set(T e);
		
		protected abstract T get(int i);
		
		protected abstract void set(int i, T e);
		
		public final int getIndex(){ return index; }
		
		public final void setIndex(int i){index = i; }
		
		/** only copies reference */
		protected abstract ArrayAccessor<T> clone();
		
		/** allocates new array */
		protected abstract ArrayAccessor<T> copy();
		
		@Override
		public String toString() {
			return String.format("[%s] at index %d", getClass().getSimpleName(), index);
		}
	}
	
	
	static class GenericAccessor<T> extends ArrayAccessor<T> {
		T[] array;
		public GenericAccessor(T[] array) {
			this.array=array;
		}
		@Override
		public T get() {return array[index];}
		@Override
		public void set(T e) {array[index] = e;}
		@Override
		protected T get(int i) {return array[i];}
		@Override
		protected void set(int i, T e) {array[i] = e;}
		@Override
		protected GenericAccessor<T> clone() {
			GenericAccessor<T> clon = new GenericAccessor<>(array);
			clon.setIndex(index);
			return clon;
		}
		@Override
		protected GenericAccessor<T> copy() {
			GenericAccessor<T> cpy = new GenericAccessor<T>(Arrays.copyOf(array, array.length));
			cpy.setIndex(index);
			return cpy;
		}
		
	}

	static class IntAccessor extends ArrayAccessor<Integer> {
		int[] array;
		public IntAccessor(int[] array) {
			this.array=array;
		}
		@Override
		public Integer get() {return array[index];}
		@Override
		public void set(Integer e) {array[index] = e;}
		@Override
		protected Integer get(int i) {return array[i];}
		@Override
		protected void set(int i, Integer e) {array[i] = e;}
		@Override
		protected IntAccessor clone() {
			IntAccessor clon = new IntAccessor(array);
			clon.setIndex(index);
			return clon;
		}
		@Override
		protected IntAccessor copy() {
			IntAccessor cpy = new IntAccessor(Arrays.copyOf(array, array.length));
			cpy.setIndex(index);
			return cpy;
		}
		
	}

	static class LongAccessor extends ArrayAccessor<Long> {
		long[] array;
		public LongAccessor(long[] array) {
			this.array=array;
		}
		@Override
		public Long get() {return array[index];}
		@Override
		public void set(Long e) {array[index] = e;}
		@Override
		protected Long get(int i) {return array[i];}
		@Override
		protected void set(int i, Long e) {array[i] = e;}
		@Override
		protected LongAccessor clone() {
			LongAccessor clon = new LongAccessor(array);
			clon.setIndex(index);
			return clon;
		}
		@Override
		protected LongAccessor copy() {
			LongAccessor cpy = new LongAccessor(Arrays.copyOf(array, array.length));
			cpy.setIndex(index);
			return cpy;
		}
		
	}

	static class FloatAccessor extends ArrayAccessor<Float> {
		float[] array;
		public FloatAccessor(float[] array) {
			this.array=array;
		}
		@Override
		public Float get() {return array[index];}
		@Override
		public void set(Float e) {array[index] = e;}
		@Override
		protected Float get(int i) {return array[i];}
		@Override
		protected void set(int i, Float e) {array[i] = e;}
		@Override
		protected FloatAccessor clone() {
			FloatAccessor clon = new FloatAccessor(array);
			clon.setIndex(index);
			return clon;
		}
		@Override
		protected FloatAccessor copy() {
			FloatAccessor cpy = new FloatAccessor(Arrays.copyOf(array, array.length));
			cpy.setIndex(index);
			return cpy;
		}
		
	}

	static class DoubleAccessor extends ArrayAccessor<Double> {
		double[] array;
		public DoubleAccessor(double[] array) {
			this.array=array;
		}
		@Override
		public Double get() {return array[index];}
		@Override
		public void set(Double e) {array[index] = e;}
		@Override
		protected Double get(int i) {return array[i];}
		@Override
		protected void set(int i, Double e) {array[i] = e;}
		@Override
		protected DoubleAccessor clone() {
			DoubleAccessor clon = new DoubleAccessor(array);
			clon.setIndex(index);
			return clon;
		}
		@Override
		protected DoubleAccessor copy() {
			DoubleAccessor cpy = new DoubleAccessor(Arrays.copyOf(array, array.length));
			cpy.setIndex(index);
			return cpy;
		}
		
	}

	static class ShortAccessor extends ArrayAccessor<Short> {
		short[] array;
		public ShortAccessor(short[] array) {
			this.array=array;
		}
		@Override
		public Short get() {return array[index];}
		@Override
		public void set(Short e) {array[index] = e;}
		@Override
		protected Short get(int i) {return array[i];}
		@Override
		protected void set(int i, Short e) {array[i] = e;}
		@Override
		protected ShortAccessor clone() {
			ShortAccessor clon = new ShortAccessor(array);
			clon.setIndex(index);
			return clon;
		}
		@Override
		protected ShortAccessor copy() {
			ShortAccessor cpy = new ShortAccessor(Arrays.copyOf(array, array.length));
			cpy.setIndex(index);
			return cpy;
		}
		
	}

	static class CharAccessor extends ArrayAccessor<Character> {
		char[] array;
		public CharAccessor(char[] array) {
			this.array=array;
		}
		@Override
		public Character get() {return array[index];}
		@Override
		public void set(Character e) {array[index] = e;}
		@Override
		protected Character get(int i) {return array[i];}
		@Override
		protected void set(int i, Character e) {array[i] = e;}
		@Override
		protected CharAccessor clone() {
			CharAccessor clon = new CharAccessor(array);
			clon.setIndex(index);
			return clon;
		}
		@Override
		protected CharAccessor copy() {
			CharAccessor cpy = new CharAccessor(Arrays.copyOf(array, array.length));
			cpy.setIndex(index);
			return cpy;
		}
		
	}

	static class ByteAccessor extends ArrayAccessor<Byte> {
		byte[] array;
		public ByteAccessor(byte[] array) {
			this.array=array;
		}
		@Override
		public Byte get() {return array[index];}
		@Override
		public void set(Byte e) {array[index] = e;}
		@Override
		protected Byte get(int i) {return array[i];}
		@Override
		protected void set(int i, Byte e) {array[i] = e;}
		@Override
		protected ByteAccessor clone() {
			ByteAccessor clon = new ByteAccessor(array);
			clon.setIndex(index);
			return clon;
		}
		@Override
		protected ByteAccessor copy() {
			ByteAccessor cpy = new ByteAccessor(Arrays.copyOf(array, array.length));
			cpy.setIndex(index);
			return cpy;
		}
		
	}

	
	
	///////////////////////////////
	// Iterator & Spliterator
	///////////////////////////////
	
	static class AccessorIterator<T> implements Iterator<ArrayAccessor<T>> {
		int i;
		final int endIndexExcl;
		final ArrayAccessor<T> acc;
		
		public AccessorIterator(int startIndex, int endIndexExcl, ArrayAccessor<T> acc) {
			this.acc = acc.clone();
			this.i = startIndex-1;
			this.endIndexExcl = endIndexExcl;
		}
		
		@Override
		public boolean hasNext() {
			return i+1 < endIndexExcl;
		}
		@Override
		public ArrayAccessor<T> next() {
			i++;
			acc.setIndex(i);
			return acc;
		}
		@Override
		public void forEachRemaining(Consumer<? super ArrayAccessor<T>> action) {
			i++;
			for(; i < endIndexExcl; i++){
				acc.setIndex(i);
			}
		}
	}
	
	static class AccessorSpliterator<T> implements Spliterator<ArrayAccessor<T>> {
		
		final ArrayAccessor<T> acc;
		int endIndex;
		final int minimumSplitSize;
		
		/**
		 * Constructs a new ImgSpliterator for the specified index range
		 * @param startIndex first index of the range (inclusive)
		 * @param endIndex last index of the range (inclusive)
		 * @param minSplitSize minimum split size for this spliterator (minimum number of elements in a split)
		 */
		AccessorSpliterator(int startIndex, int endIndex, int minSplitSize, ArrayAccessor<T> acc) {
			this.acc = acc.clone();
			this.acc.setIndex(startIndex);
			this.endIndex = endIndex;
			this.minimumSplitSize = minSplitSize;
		}
		
		private void setEndIndex(int endIndex) {
			this.endIndex = endIndex;
		}

		@Override
		public boolean tryAdvance(final Consumer<? super ArrayAccessor<T>> action) {
			if(acc.getIndex() <= endIndex){
				int index = acc.getIndex();
				action.accept(acc);
				acc.setIndex(index+1);
				return true;
			} else {
				return false;
			}
		}
		
		@Override
		public void forEachRemaining(final Consumer<? super ArrayAccessor<T>> action) {
			int idx = acc.getIndex();
			for(;idx <= endIndex; acc.setIndex(++idx)){
				action.accept(acc);
			}
		}

		@Override
		public Spliterator<ArrayAccessor<T>> trySplit() {
			int currentIdx = Math.min(acc.getIndex(), endIndex);
			int midIdx = currentIdx + (endIndex-currentIdx)/2;
			if(midIdx > currentIdx+minimumSplitSize){
				AccessorSpliterator<T> split = new AccessorSpliterator<T>(midIdx, endIndex, minimumSplitSize, acc);
				setEndIndex(midIdx-1);
				return split;
			} else {
				return null;
			}
		}

		@Override
		public long estimateSize() {
			int currentIndex = acc.getIndex();
			int lastIndexPlusOne = endIndex+1;
			return lastIndexPlusOne-currentIndex;
		}

		@Override
		public int characteristics() {
			return NONNULL | SIZED | CONCURRENT | SUBSIZED | IMMUTABLE;
		}
		
	}

}
