set -e
JBOSS_HOME="/home/dvaldivieso/JBOSS/jboss-6.1.0.Final"
DEV_FOLDER="/home/dvaldivieso/workspace4/terranvm"
ANT_TERRA="/home/dvaldivieso/workspace4/terranvm/ANT_TERRA"


rm -rf $JBOSS_HOME/server/default/deploy/terranvm/
rm -rf $JBOSS_HOME/server/default/deploy/terranvm.war
rm -rf $ANT_TERRA/dist/terranvm.war

rm -rf $ANT_TERRA/resources/WEB-INF/pages.xml
cp -f $DEV_FOLDER/WebContent/WEB-INF/pages.xml $ANT_TERRA/resources/WEB-INF/.

rm -rf $ANT_TERRA/resources/WEB-INF/web.xml
cp -f $DEV_FOLDER/WebContent/WEB-INF/web.xml $ANT_TERRA/resources/WEB-INF/.

rm -rf $ANT_TERRA/resources/WEB-INF/components.xml
cp -f $DEV_FOLDER/WebContent/WEB-INF/components.xml $ANT_TERRA/resources/WEB-INF/.

rm -rf $ANT_TERRA/resources/META-INF/persistence-prod.xml
cp -f $DEV_FOLDER/src/main/META-INF/persistence.xml $ANT_TERRA/resources/META-INF/persistence-prod.xml

rm -rf $ANT_TERRA/resources/messages_en.properties
cp -f $DEV_FOLDER/src/main/messages_en.properties $ANT_TERRA/resources/.

cp $DEV_FOLDER/WebContent/WEB-INF/lib/*.jar $ANT_TERRA/lib/
cp $JBOSS_HOME/server/default/deployers/jsf.deployer/Mojarra-2.0/jsf-libs/* $ANT_TERRA/lib/

rm -rf $ANT_TERRA/view/
mkdir $ANT_TERRA/view
cp -r $DEV_FOLDER/WebContent/* $ANT_TERRA/view/.
rm -rf $ANT_TERRA/view/WEB-INF/classes
rm -rf $ANT_TERRA/view/WEB-INF/dev

rm -rf $ANT_TERRA/src
cp -r $DEV_FOLDER/src $ANT_TERRA/.
rm -rf $ANT_TERRA/src/main/*.sql 
rm -rf $ANT_TERRA/src/main/*.properties 
rm -rf $ANT_TERRA/src/main/*.drl 
rm -rf $ANT_TERRA/src/hot/*.properties 
mkdir $ANT_TERRA/src/test

cd $ANT_TERRA
ant deploy

rm -rf $ANT_TERRA/view/*
rm -rf $ANT_TERRA/src/
rm -rf $ANT_TERRA/exploded-archives/terranvm.war
rm -rf $ANT_TERRA/resources/WEB-INF/pages.xml
rm -rf $ANT_TERRA/resources/WEB-INF/web.xml
rm -rf $ANT_TERRA/resources/WEB-INF/components.xml
rm -rf $ANT_TERRA/resources/META-INF/persistence-prod.xml
rm -rf $ANT_TERRA/resources/messages_en.properties

bash $JBOSS_HOME/bin/run.sh
rm -rf $JBOSS_HOME/server/default/deploy/terranvm/
rm -rf $JBOSS_HOME/server/default/deploy/terranvm.war
