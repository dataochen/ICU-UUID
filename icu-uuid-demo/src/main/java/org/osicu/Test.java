package org.osicu;

public class Test {
    public static void main(String[] args) throws Exception {
//        WorkerIdGenerateInterface object = SpiFactory.getObject(WorkerIdGenerateInterface.class);
//        object.callBack4iiLegalWorkIdStrategy();
        Class<?>[] interfaces = TestInterface.class.getInterfaces();
        System.out.println(interfaces);
    }
}
