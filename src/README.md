# CNT-Project
Group Number: 50

<h3>Team Members</h3>
- Priyal Patel - priyal.patel@ufl.edu <br />
- Brianna Rodriguez - b.rodriguez1@ufl.edu <br />
- Maya Singh - singh.maya@ufl.edu <br />

<h3>Overview of the Project</h3>
This project implements a P2P file-sharing software for file distribution. The following protocols are implemented: <br />
- Handshake: When a peer connects to another peer  <br />
- Bitfield: To let a peer know which file pieces it has (done in the beginning)   <br />
- Request - when a peer is requesting a piece of a specific index that it is interested in   <br />
- piece - when a peer receives a piece   <br />
- choke - when a peer is choked by another peer that it is interested in but that peer does not have this peer as its preferred neighbor   <br />
- unchoke - when a peer can receive pieces from a peer that it is interested in   <br />
- Have - when another peer gets a piece, it will send "have" messages to all other peers so that the other peers can update and keep track of every peer's bitfield (used in termination)   <br />
- interested - when a peer is interested in this peer's file pieces   <br />
- Not interested - when a peer is not interested in this peer's file pieces   <br />

<h3> Video Link </h3>
https://youtu.be/T0a6r-qf1mQ

<h3>Contribution of Each Team Member</h3>
- Priyal Patel - implemented TCP Connection, handshake, Message handling (request, have, interested/not interested, choke, unchoke, piece, bitfield), logs, file handling <br />
- Brianna Rodriguez - implemented termination of sockets, reading peerInfo.cfg <br />
- Maya Singh - implemented kPreferredNeighbors, choking, unchoking, optimistically unchoke, reading common.cfg, getMessage, file handling <br />

<h3>How to Run Project (Locally)</h3>
1. Make sure you are in the src directory and that the file in Peer class in Peer folder is set to "localPeerInfo.cfg" <br />
2. Run 'make' in the terminal to compile the classes <br />
3. For x amount of processes you want to run open x amount of terminals <br />
4. Run 'java Peer/peerProcess 100[x]' in each terminal where x is the sequential number of processes. For instance, the first process will be 1001 and so on. <br />

<h3>How to Run Project (Linux Machine)</h3>
1. Ensure you have access to STORM from CISE and SSH into it <br />
2. Make sure you are in the src directory and that the file in Peer class in Peer folder is set to "PeerInfo.cfg" <br />
3. Run 'make' in the terminal to compile the classes <br />
4. SSH into the linux machine provided in the "PeerInfo.cfg" file <br />
5. For x amount of processes you want to run open x amount of terminals <br />
6. Run 'java Peer/peerProcess 100[x]' in each terminal where x is the sequential number of processes. For instance, the first process will be 1001 and so on. <br />


