**Install java 8**  
>sudo apt install openjdk-8-jdk openjdk-8-jre

>java --version

nano /etc/environment  
JAVA_HOME= /usr/lib/jvm/java-8-openjdk-amd64  
JRE_HOME=/usr/lib/jvm/java-8-openjdk-amd64/jre  

**Download kafka**  
Goto below link and download  
https://www.apache.org/dyn/closer.cgi?path=/kafka/2.4.1/kafka_2.13-2.4.1.tgz  
Click on the mirror link and save file

After download complete , goto downloads folder (/home/padma/Downloads) and run ls command, you should see file kafka_2.13-2.4.1.tgz  

Extract file using command   
>tar -xvf kafka_2.13-2.4.1.tgz
> 
Goto folder **/home/padma/Downloads/kafka_2.13-2.4.1/bin**  
>./kafka-topics.sh
>  
if above command returns help documentation then kafka installed successfully  

Update path
Go to user home directory (/home/padma)  
Edit .bashrc file to add kafka to PATH

>vim .bashrc
>
add below line at the end  
**export PATH=/home/padma/Downloads/kafka_2.13-2.4.1/bin:$PATH**  
save and close the file , verify using **cat .bashrc** command  

Open new terminal and run below command  form any path
**kafka-topics.sh**  
if everything goes well , we should see kafka help documentation  

**Changing default data directory for Zookeeper and Kafka**  
create **data** folder inside kafka home folder (/home/padma/Downloads/kafka_2.13-2.4.1) using command **mkdir data**  
go to data folder and create another folder inside for zookeeper **mkdir data/zookeeper**  

Edit Zookeeper properties to use the data folder  
from kafka home folder , run command **vim config/zookeeper.properties**  
edit the **dataDir** property to below 
**dataDir=/home/padma/Downloads/kafka_2.13-2.4.1/data/zookeeper**  
Save the file and verify using **cat config/zookeeper.properties** command  

Start Zookeeper server and verify  
>zookeeper-server-start.sh config/zookeeper.properties
>
if everything goes well , we should see below log in console  
**INFO binding to port 0.0.0.0/0.0.0.0:2181**  

Edit kafka properties to use the data folder
from kafka home folder, create data folder for kafka **mkdir data/kafka**  
run command **vim config/server.properties**  
change the property to **log.dirs=/home/padma/Downloads/kafka_2.13-2.4.1/data/kafka**  

from kafka home directory run below command to run kafka server
>kafka-server-start.sh config/server.properties
> 
if everything goes well , we should see below log in the terminal  
**INFO [KafkaServer id=0] started (kafka.server.KafkaServer)**  
go to folder  /home/padma/Downloads/kafka_2.13-2.4.1/data/kafka , 5 files should be there.  
**Note : Zookeeper should be running before kafka server run command**  


**Create New topic in kafka**  
>kafka-topics.sh --zookeeper 127.0.0.1:2181 --topic first_topic --create --partitions 3 --replication-factor 1  

**List all topics in kafka**   
>kafka-topics.sh --zookeeper 127.0.0.1:2181 --list  

**Describe specific topic in kafka**  
>kafka-topics.sh --zookeeper 127.0.0.1:2181 --topic first_topic --describe  

**Delete topic in kafka**  
>kafka-topics.sh --zookeeper 127.0.0.1:2181 --topic first_topic --delete  


**Send message using console producer**  
>kafka-console-producer.sh --broker-list 127.0.0.1:9092 --topic first_topic  
>\>send message here  
>\>send another message

Setting producer properties  
>kafka-console-producer.sh --broker-list 127.0.0.1:9092 --topic first_topic --producer-property acks=all  

Send message to non existing topic  
>kafka-console-producer.sh --broker-list 127.0.0.1:9092 --topic new_topic    

_[2020-04-05 22:22:49,633] WARN [Producer clientId=console-producer] Error while fetching metadata with correlation id 3 : {new_topic=LEADER_NOT_AVAILABLE} (org.apache.kafka.clients.NetworkClient)  
[2020-04-05 22:22:49,715] WARN [Producer clientId=console-producer] Error while fetching metadata with correlation id 4 : {new_topic=LEADER_NOT_AVAILABLE} (org.apache.kafka.clients.NetworkClient)_  

new topic will be created with default settings , always create topic before using it  

**Receive message using console consumer**  
>kafka-console-consumer.sh --bootstrap-server 127.0.0.1:9092 --topic first_topic  
>  
above command only reads message from th point it launched , it wont read old messages  

>kafka-console-consumer.sh --bootstrap-server 127.0.0.1:9092 --topic first_topic --from-beginning  
>  
above command read all messages in the topic from beginning  , reads old messages  

**Consumer Groups**  
if we launch multiple consumers under same group , they will share the messages from same topic. we can do consumer load balancing in this way  

launch below consumer from multiple  terminals  
>kafka-console-consumer.sh --bootstrap-server 127.0.0.1:9092 --topic first_topic --group my-first-application  
>  
send messages from producer , we can notice all there consumers receive messages from same topic in round robin way  
  
describe consumer groups using below command  
>kafka-consumer-groups.sh --bootstrap-server localhost:9092 --describe  --group my-first-application  
>

**Reset offsets**   
Reset offsets is a mechanism where can set index in topic where consumer can start reading from that index
this is helpful when we want to read all the messages from beginning from a topic   

>kafka-consumer-groups.sh --bootstrap-server localhost:9092 --group my-first-application
 --reset-offsets --to-earliest --execute --topic first_topic  

**Note: Check documentation for other options**  

**Kafka GUI Tool**  
If you are interested in using GUI tool, please check below link  
http://kafkatool.com/download.html  



**setup twitter account and generate access keys**  
go to  https://developer.twitter.com/en and follow the the process of registration and create app
and get the below information from twitter app  

Access Token
Access Token Secret

API key
API secret key

once we got the above information , its time to setup project  
find out twitter client library from git hub.
> https://github.com/twitter/hbc  

add twitter dependency to project mentioned in above git-hub link.



**Producer Acks**  
acks = 0 (no acks) - set this if its OK to loose data  
acks = 1 (default , leader acknowledgement) - leader receives data, no guarantee replicas received data  
acks = all (slow and safe)- both leader and replica set received data  high safety high latency  

Safe producers always set to acks=all

**Producer Retries**  
by default retries set to very high number  
retries = Interger.MAX  
retry time  
retry.backoff.ms = 100 ms by default  

during retries  messages will be sent out of order(mostly in batching )     
for key based ordering this may be issue , to handle this  
max.in.flight.requests.per.connection = 5 (default)  
set this to 1 ensure ordering , but impacts throughput  

**Producer Timeouts**  
delivery.timeout.ms = 120000 (default)  
producer wait for 2 mins before failing a message  

**Idempotent Producers**  
 Idempotent producers handles the duplicate messages sent by producers due to network errors  
 in order to convert producer to idempotent producer , need to set below proprty to producer   
 >producerProps.put(enable.idempotence, true);  

above property will set te below properties internally   

retries = Integer.MAX_VALUE;  
max.in.flight.requests = 5;  
acks = all;  

**Safe Producer**   
if we set below properties, then producer is safe producer  
enable.idempotence = true (at producer level)    
min.insync.replicas = 2 (at topic or broker level)

Running safe producer has impact on throughput and latency  

**High Throughput producer**  
Message compression : Compressing message at producer side will  improve throughput, less latency, batter disk utilization  
Linger Ms: Number of milliseconds producer wait before sending a batch  , default 0  
Batch size : maximum number of bytes included in message default 16 kb  


**setup elastic search and generate access keys**
We can create elastic search instance using below cloud provider  

>https://app.bonsai.io  

Sign up for free sandbox cluster and get the access keys.

From the dashboard , go to interactive console and run below commands  

To see the list of indices
> GET /_cat/indices?v  

To create new index (in this case create new index with name twitter)
> PUT /twitter  

To create a document (in this case we are creating tweets under twitter , document with id 1)  
>PUT /twitter/tweets/1  
>{ "course" : "kafka course 1"  }  

To see the created document 
>GET /twitter/tweets/1  

To check the count of documents 
>GET /twitter/tweets/_count  

TO delete all documents 
>DELETE   /twitter/tweets/_all  


Add maven dependency to java project

>https://www.elastic.co/guide/en/elasticsearch/client/java-rest/master/java-rest-high-getting-started-maven.html  

Hostname , username and password get from the access tab in bonsai dashboard  
> https://username:password@hostname:443 


**Consumer Advanced Configurations**  

Delivery Semantics: 

**At Most Once :** offsets are committed as soon as consumer received message batch , if consumer processing goes wrong message will be lost.  
**At Least Once(Default) :** offsets are committed after message processed by consumer , if consumer process goes wrong, there is a chance of duplicate message processing.  
**Exactly Once :** can be achieved by kafka streams and Idempotent consumers.

Offset commit strategies:  
>enable.auto.commit = true && synchronous processing of batches  
>enable.auto.commit = false && manually commit offsets.  

Consumer Heartbeat Thread:

>session.timeout.ms (default 10 sec)  

Heartbeats are sent periodically to broker  
if no heartbeat sent during that period , consumer considered dead.  

>heartbeat.interval.ms (default to 3 sec)  
  
usually set to 1/3rd of session time out.  

   


  
   
   
  


  




  

   






  
    










 

      

  





 

 


 
 