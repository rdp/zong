zongdir=../Zong/Programm/Zong

rm -rf dist
cp $zongdir/build.xml .

for i in build core installer lib midi-out musicxml musicxml-in pdlib player shared util viewer
  do
    rm -rf $i
    cp -r $zongdir/$i .
done
