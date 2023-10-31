package list;

import java.util.Arrays;

public class ArrayList<E> implements List<E> {
    /*
    생성자
     */
    private static final int DEFAULT_CAPACITY = 10;
    private static final Object[] EMPTY_ARRAY = {};

    private int size;
    Object[] array;

    public ArrayList() {
        this.array = EMPTY_ARRAY;
        this.size = 0;
    }

    public ArrayList(int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException("capacity: " + capacity);
        }
        this.array = new Object[capacity];
        this.size = 0;
    }

    /**
     * 동적 할당을 위한 용량 최적화 전략 메소드
     */
    private void resize(){
        int array_capacity = array.length;
        /*
        배열이 비어있을 때
         */
        if(Arrays.equals(array, EMPTY_ARRAY)){
            array = new Object[DEFAULT_CAPACITY];
            return;
        }
        /*
        배열이 꽉 찼을 때
         */
        if(size == array_capacity){
            int new_capacity = array_capacity * 2;
            array = Arrays.copyOf(array, new_capacity);
            return;
        }
        /*
        배열이 반 이하로 찼을 때
         */
        if(size < (array_capacity / 2)){
            int new_capacity = array_capacity / 2;

            array = Arrays.copyOf(array, new_capacity);
            return;
        }
    }
    /*
    add 메소드
     */
    @Override
    public boolean add(E value) {
        addLast(value);
        return true;
    }

    private void addLast(E value) {
        // 만약 배열이 꽉 찼다면, 배열의 크기를 두 배로 늘린다.
        if(size == array.length){
            resize();
        }

        array[size] = value;
        size++;
    }

    /*
    add 중간 할당
     */
    @Override
    public void add(int index, E value) {

        if(index < 0 || index > size){
            throw new IndexOutOfBoundsException("index: " + index);
        }
        if(index == size){
            addLast(value);
        }else{
            if(size == array.length){
                resize();
            }
            /*
            뒤로 미는 작업
            1 2 3 4 5
            array[4] = array[3];
            array[3] = array[2];
            array[2] = array[1];
             */
            for(int i = size; i > index; i--){
                array[i] = array[i - 1];
            }
            array[index] = value;
            size++;
        }
    }
    /*
    addFirst 메소드 전체 뒤로 밀기.
     */
    public void addFirst(E value){
        add(0, value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public E remove(int index) {

        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("index: " + index);
        }

        E element = (E) array[index];
        array[index] = null;

        for(int i = index; i < size - 1; i++){
            array[i] = array[i + 1];
            array[i + 1] = null;
        }

        size--;
        resize();
        return element;
    }

    @Override
    public boolean remove(Object value) {

        int index = indexOf(value);

        if(index >= 0){
            remove(index);
            return true;
        }

        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    /*
    type safe 안정성에 대한 경고 무시
    E 타입으로 casting 중이며, 그 대상은 Object 타입이다.
    Object -> E 타입 변환 시 ClassCastException 발생 가능성 경고가 뜬다.
    하지만 우리는 E 타입으로 casting 하고 있다는 것을 알고 있으므로
    경고를 무시하고 진행하는 것을 말한다.
     */
    public E get(int index) {

        if(index < 0 || index >= size){
            throw new IndexOutOfBoundsException("index: " + index);
        }
        return (E) array[index];
    }


    /*
    기존의 index에 위치한 데이터를 새로운 데이터로 변환 하는 것을 말한다. set은 교체라고 보면 된다.
     */
    @Override
    public void set(int index, E value) {
        if(index < 0 || index >= size){
            throw new IndexOutOfBoundsException("index: " + index);
        }
        array[index] = value;
    }

    @Override
    public boolean contains(Object value) {
        /*
        0 이상이면 데이터가 존재한다는 뜻 왜냐하면 indexOf는 데이터가 없으면 -1을 반환하기 때문이다.
         */
        if(indexOf(value) >= 0){
            return true;
        }

        return false;
    }

    /*
    indexOf는 특정 요소가 몇 번째 위치에 있는지를 반환한다.
    요소가 없으면 -1을 반환한다.
     */
    @Override
    public int indexOf(Object value) {
        int i = 0;

        // index가 0부터 시작하므로 i = 0

        for (i = 0; i < size; i++){
            if (array[i].equals(value)){
                return i;
            }
        }

        return -1;
    }
    /*
    lastIndexOf는 특정 요소가 몇 번째 위치에 있는지를 반환한다.
    뒤에서 부터 검색한다.
     */
    public int lastIndexOf(Object value){
        for(int i = size - 1; i >= 0; i--){
            if(array[i].equals(value)){
                return i;
            }
        }
        return -1;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /*
    왜 절반으로 리사이징을 하는가에 대한 생각은,
    용적량은 10부터 2배 늘어난다. 따라서 현재 용적량에 따르고 resize를 하는 것
     */
    @Override
    public void clear() {

            for(int i = 0; i < size; i++){
                array[i] = null;
            }

            size = 0;
            resize();
    }
}
