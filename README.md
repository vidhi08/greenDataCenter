# greenDataCenter
Green data centre is a project which aims at optimizing the use of servers in a data centre.
When requests are received by the server, they are handled on a first come, first serve basis.
However a threshold capacity which is below the full capacity of the server to avoid overheating;
when this threshold has been reached, the system will divert the incoming requests to the other servers


The system is capable of monitoring traffic, activating or deactivating servers accordingly based
on demands, redirecting new requests to others servers based on threshold capacity and splitting 
big jobs over different servers.
