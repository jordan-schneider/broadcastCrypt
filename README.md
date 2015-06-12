# broadcastCrypt
Experimentally finds key assignments for broadcast encryption schemes.

If you want to run this yourself, there are a few changes in code you need to make. You have to specify how many cores your computer has, so that it can properly multithread.

All data is in the \data folder in the format data[n,s,w].txt. Each line contains a 2D array of key assignments, followed by a comma, followed by the maximum bandwidth required for the scheme. Example:
[[0, 1][0, 2][1, 2]], 2

There is also a summary.txt file which has quadruplets of n, s, w, and b. Example:
n=3 s=2 w=4 b=2

I might want to change the formatting on these to make it a csv or something. The first one seems harder because of nesting arrays.