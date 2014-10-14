# build-demo.sh
rm bin/common/Initiative.class
rm bin/EarthSim/*.class
javac -cp bin -d bin -source 1.6 src/common/Initiative.java src/common/ComponentBase.java src/EarthSim/*.java
