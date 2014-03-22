/**
 * Copyright 2011 Micheal Swiggs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.monacoin.net.discovery;

import com.google.monacoin.core.NetworkParameters;

import javax.annotation.Nullable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

/**
 * SeedPeers stores a pre-determined list of Bitcoin node addresses. These nodes are selected based on being
 * active on the network for a long period of time. The intention is to be a last resort way of finding a connection
 * to the network, in case IRC and DNS fail. The list comes from the Bitcoin C++ source code.
 */
public class SeedPeers implements PeerDiscovery {
    private NetworkParameters params;
    private int pnseedIndex;

    public SeedPeers(NetworkParameters params) {
        this.params = params;
    }

    /**
     * Acts as an iterator, returning the address of each node in the list sequentially.
     * Once all the list has been iterated, null will be returned for each subsequent query.
     *
     * @return InetSocketAddress - The address/port of the next node.
     * @throws PeerDiscoveryException
     */
    @Nullable
    public InetSocketAddress getPeer() throws PeerDiscoveryException {
        try {
            return nextPeer();
        } catch (UnknownHostException e) {
            throw new PeerDiscoveryException(e);
        }
    }

    @Nullable
    private InetSocketAddress nextPeer() throws UnknownHostException {
        if (pnseedIndex >= seedAddrs.length) return null;
        return new InetSocketAddress(convertAddress(seedAddrs[pnseedIndex++]),
                params.getPort());
    }

    /**
     * Returns an array containing all the Bitcoin nodes within the list.
     */
    public InetSocketAddress[] getPeers(long timeoutValue, TimeUnit timeoutUnit) throws PeerDiscoveryException {
        try {
            return allPeers();
        } catch (UnknownHostException e) {
            throw new PeerDiscoveryException(e);
        }
    }

    private InetSocketAddress[] allPeers() throws UnknownHostException {
        InetSocketAddress[] addresses = new InetSocketAddress[seedAddrs.length];
        for (int i = 0; i < seedAddrs.length; ++i) {
            addresses[i] = new InetSocketAddress(convertAddress(seedAddrs[i]), params.getPort());
        }
        return addresses;
    }

    private InetAddress convertAddress(int seed) throws UnknownHostException {
        byte[] v4addr = new byte[4];
        v4addr[0] = (byte) (0xFF & (seed));
        v4addr[1] = (byte) (0xFF & (seed >> 8));
        v4addr[2] = (byte) (0xFF & (seed >> 16));
        v4addr[3] = (byte) (0xFF & (seed >> 24));
        return InetAddress.getByAddress(v4addr);
    }

    public static int[] seedAddrs =
            {
		0x4774c836, 0x082b20b4, 0x156b91b4, 0x86cb079d, 0xde257899, 0x48037899, 0x5d33f285, 0xe132f285,
		0x7e17f285, 0xf713f285, 0x0d56ed80, 0x11217b7e, 0x6e90767e, 0xf690367d, 0xb905357d, 0xcd4f297c,
		0x29616f79, 0xd3a13f77, 0x50cef176, 0x20c39a76, 0x7b0d6c75, 0x668c1e73, 0xf6409a71, 0xbeb5e96f,
		0x1088153d, 0x03517b3d, 0xfd35263c, 0x595ff131, 0x4854d431, 0x3df85edb, 0xddf85edb, 0x59eb5edb,
		0x1b496adb, 0x61ff83d3, 0x529cb0b7, 0xc9dcaab6, 0x0874553b, 0x8693507e, 0xfc23547c, 0xdb7d1176,
		0x82d696de, 0x41f0aa99, 0xa5c0a099, 0x6945f9c0, 0x290af285, 0x6aabd13d, 0xe9deee3c, 0xd00b8a3a,
		0x33c7a799, 0x412b7999, 0x12351e7d, 0x114c007b, 0x82f31276, 0xc55b1176, 0x37094a75, 0x8aa43a74,
		0xa1feaa72,
            };

    public void shutdown() {
    }
}
