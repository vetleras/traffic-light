# use with "sudo bash run.sh <name of the file with no extension>"

#export DISPLAY=:0 # open the gui windows on the pi, not in the ssh terminal

if sudo javac -classpath .:classes:/opt/pi4j/lib/'*' exercises/trafficlight/$1.java; then
    echo "Java program '$1' successfully compiled"
    sudo java -classpath .:classes:/opt/pi4j/lib/'*' exercises.trafficlight.$1
else
    echo "Java program '$1' compilation failed"
fi
