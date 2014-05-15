import os

trials = 40

filebase = '400nodes/sybil'


try:
    os.system("buck build proximitySimulationNoGUI")
except:
    exit()

for i in range(trials):
    os.system("java -jar -Xms6G buck-out/gen/apps/simulation/proximitySimulationNoGUI.jar -p > results/"+filebase+"/sybilPopular"+str(i)+".json")
    os.system("java -jar -Xms6G buck-out/gen/apps/simulation/proximitySimulationNoGUI.jar -r > results/"+filebase+"/sybilAverage"+str(i)+".json")
    os.system("java -jar -Xms6G buck-out/gen/apps/simulation/proximitySimulationNoGUI.jar -u > results/"+filebase+"/sybilUnpopular"+str(i)+".json")
    os.system("java -jar -Xms6G buck-out/gen/apps/simulation/proximitySimulationNoGUI.jar -a > results/"+filebase+"/sybilSybil"+str(i)+".json")

