package array;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ProtoSlice<T> implements Iterable<ProtoSlice.ArrayAccessor<T>>{
	
	final ArrayAccessor<T> aa;
	final int size;
	final int beginIdx;
	
	private ProtoSlice(ArrayAccessor<T> accessor, int beginIdx, int size) {
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
	
	public ProtoSlice<T> copy() {
		return new ProtoSlice<T>(aa.copy(),beginIdx,size);
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
	
	////MKNATIVE>>>>
	static /*RM*/<T>/**/ ProtoSlice</*G*/T/**/> get(/*N*/T/**/[] array, int beginIdx, int length){
		return new ProtoSlice</*G*/T/**/>(new /*RPLC:Generic*/GenericAccessor/**//*RM*/<T>/**/(array), beginIdx, length);
	}
	
	static /*RM*/<T>/**/ ProtoSlice</*G*/T/**/> get(/*N*/T/**/[] array){
		return get(array, 0, array.length);
	}
	////<<<<
	
	///////////////////////////////
	// Static Streaming
	///////////////////////////////
	
	static int estimateReasonableSplitSize(int length) {
		final int tasksPerProcessor = 16;
		final int concurrentLimit = Runtime.getRuntime().availableProcessors()*tasksPerProcessor;
		final int minReasonable = 512;
		return Math.max(minReasonable, Integer.highestOneBit(length/concurrentLimit));
	}
	
	////MKNATIVE>>>>
	public static /*RM*/<T>/**/ Stream<ArrayAccessor</*G*/T/**/>> stream(/*N*/T/**/[] array, boolean parallel, int beginIndex, int length, int minSplitSize){
		return StreamSupport.stream(new AccessorSpliterator</*G*/T/**/>(beginIndex, beginIndex+length-1, minSplitSize, new /*RPLC:Generic*/GenericAccessor/**//*RM*/<T>/**/(array)), parallel);
	}
	
	public static /*RM*/<T>/**/ Stream<ArrayAccessor</*G*/T/**/>> stream(/*N*/T/**/[] array, boolean parallel, int beginIndex, int length){
		return stream(array, parallel, beginIndex, length, estimateReasonableSplitSize(length));
	}
	
	public static /*RM*/<T>/**/ Stream<ArrayAccessor</*G*/T/**/>> stream(/*N*/T/**/[] array, boolean parallel){
		return stream(array, parallel, 0, array.length);
	}
	////<<<<
	
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
	
	
	////MKNATIVE>>>>
	static class /*RPLC:Generic*/GenericAccessor/**//*RM*/<T>/**/ extends ArrayAccessor</*G*/T/**/> {
		/*N*/T/**/[] array;
		public /*RPLC:Generic*/GenericAccessor/**/(/*N*/T/**/[] array) {
			this.array=array;
		}
		@Override
		public /*G*/T/**/ get() {return array[index];}
		@Override
		public void set(/*G*/T/**/ e) {array[index] = e;}
		@Override
		protected /*G*/T/**/ get(int i) {return array[i];}
		@Override
		protected void set(int i, /*G*/T/**/ e) {array[i] = e;}
		@Override
		protected /*RPLC:Generic*/GenericAccessor/**//*RM*/<T>/**/ clone() {
			/*RPLC:Generic*/GenericAccessor/**//*RM*/<T>/**/ clon = new /*RPLC:Generic*/GenericAccessor/**//*RM*/<>/**/(array);
			clon.setIndex(index);
			return clon;
		}
		@Override
		protected /*RPLC:Generic*/GenericAccessor/**//*RM*/<T>/**/ copy() {
			/*RPLC:Generic*/GenericAccessor/**//*RM*/<T>/**/ cpy = new /*RPLC:Generic*/GenericAccessor/**//*RM*/<T>/**/(Arrays.copyOf(array, array.length));
			cpy.setIndex(index);
			return cpy;
		}
		
	}
	////<<<<
	
	
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