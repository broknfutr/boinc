/*
 * This file is part of BOINC.
 * http://boinc.berkeley.edu
 * Copyright (C) 2020 University of California
 *
 * BOINC is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * BOINC is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with BOINC.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.berkeley.boinc.rpc;

import android.util.Xml;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.xml.sax.SAXException;

import static org.junit.Assert.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Xml.class)
public class AcctMgrRPCReplyParserTest {
    private static final String MESSAGE = "Message";

    private AcctMgrRPCReplyParser acctMgrRPCReplyParser;
    private AcctMgrRPCReply expected;

    @Before
    public void setUp() {
        acctMgrRPCReplyParser = new AcctMgrRPCReplyParser();
        expected = new AcctMgrRPCReply();
    }

    @Test
    public void testParse_whenRpcStringIsNull_thenExpectNullPointerException() {
        mockStatic(Xml.class);

        assertThrows(NullPointerException.class, () -> AcctMgrRPCReplyParser.parse(null));
    }

    @Test
    public void testParse_whenRpcStringIsEmpty_thenExpectNull() {
        mockStatic(Xml.class);

        assertNull(AcctMgrRPCReplyParser.parse(""));
    }

    @Test
    public void testParser_whenOnlyStartElementIsRun_thenExpectElementStarted() throws SAXException {
        acctMgrRPCReplyParser.startElement(null, "", null, null);

        assertTrue(acctMgrRPCReplyParser.mElementStarted);
    }

    @Test
    public void testParser_whenBothStartElementAndEndElementAreRun_thenExpectElementNotStarted() throws SAXException {
        acctMgrRPCReplyParser.startElement(null, AcctMgrRPCReplyParser.ACCT_MGR_RPC_REPLY_TAG, null, null);
        acctMgrRPCReplyParser.endElement(null, AcctMgrRPCReplyParser.ACCT_MGR_RPC_REPLY_TAG, null);

        assertFalse(acctMgrRPCReplyParser.mElementStarted);
    }

    @Test
    public void testParser_whenLocalNameIsEmpty_thenExpectNullEntity() throws SAXException {
        acctMgrRPCReplyParser.startElement(null, "", null, null);

        assertNull(acctMgrRPCReplyParser.getAccountMgrRPCReply());
    }

    @Test
    public void testParser_whenXmlAccountManagerRPCReplyWithNoElements_thenExpectDefaultEntity()
            throws SAXException {
        acctMgrRPCReplyParser.startElement(null, AcctMgrRPCReplyParser.ACCT_MGR_RPC_REPLY_TAG, null, null);
        acctMgrRPCReplyParser.endElement(null, AcctMgrRPCReplyParser.ACCT_MGR_RPC_REPLY_TAG, null);

        assertEquals(expected, acctMgrRPCReplyParser.getAccountMgrRPCReply());
    }

    @Test
    public void testParser_whenXmlAccountManagerRPCReplyWithOnlyErrorNum_thenExpectMatchingEntity()
            throws SAXException {
        acctMgrRPCReplyParser.startElement(null, AcctMgrRPCReplyParser.ACCT_MGR_RPC_REPLY_TAG, null, null);
        acctMgrRPCReplyParser.startElement(null, AcctMgrRPCReplyParser.ERROR_NUM_TAG, null, null);
        acctMgrRPCReplyParser.characters("1".toCharArray(), 0, 1);
        acctMgrRPCReplyParser.endElement(null, AcctMgrRPCReplyParser.ERROR_NUM_TAG, null);
        acctMgrRPCReplyParser.endElement(null, AcctMgrRPCReplyParser.ACCT_MGR_RPC_REPLY_TAG, null);

        expected.error_num = 1;

        assertEquals(expected, acctMgrRPCReplyParser.getAccountMgrRPCReply());
    }

    @Test
    public void testParser_whenXmlAccountManagerRPCReplyHasErrorNumAndOneMessage_thenExpectMatchingEntity()
            throws SAXException {
        acctMgrRPCReplyParser.startElement(null, AcctMgrRPCReplyParser.ACCT_MGR_RPC_REPLY_TAG, null, null);
        acctMgrRPCReplyParser.startElement(null, AcctMgrRPCReplyParser.ERROR_NUM_TAG, null, null);
        acctMgrRPCReplyParser.characters("1".toCharArray(), 0, 1);
        acctMgrRPCReplyParser.endElement(null, AcctMgrRPCReplyParser.ERROR_NUM_TAG, null);
        acctMgrRPCReplyParser.startElement(null, AcctMgrRPCReplyParser.MESSAGE_TAG, null, null);
        acctMgrRPCReplyParser.characters(MESSAGE.toCharArray(), 0, MESSAGE.length());
        acctMgrRPCReplyParser.endElement(null, AcctMgrRPCReplyParser.MESSAGE_TAG, null);
        acctMgrRPCReplyParser.endElement(null, AcctMgrRPCReplyParser.ACCT_MGR_RPC_REPLY_TAG, null);

        expected.error_num = 1;
        expected.messages.add(MESSAGE);

        assertEquals(expected, acctMgrRPCReplyParser.getAccountMgrRPCReply());
    }

    @Test
    public void testParser_whenXmlAccountManagerRPCReplyHasErrorNumAndTwoMessages_thenExpectMatchingEntity()
            throws SAXException {
        acctMgrRPCReplyParser.startElement(null, AcctMgrRPCReplyParser.ACCT_MGR_RPC_REPLY_TAG, null, null);
        acctMgrRPCReplyParser.startElement(null, AcctMgrRPCReplyParser.ERROR_NUM_TAG, null, null);
        acctMgrRPCReplyParser.characters("1".toCharArray(), 0, 1);
        acctMgrRPCReplyParser.endElement(null, AcctMgrRPCReplyParser.ERROR_NUM_TAG, null);
        acctMgrRPCReplyParser.startElement(null, AcctMgrRPCReplyParser.MESSAGE_TAG, null, null);
        acctMgrRPCReplyParser.characters((MESSAGE + " 1").toCharArray(), 0, MESSAGE.length() + 2);
        acctMgrRPCReplyParser.endElement(null, AcctMgrRPCReplyParser.MESSAGE_TAG, null);
        acctMgrRPCReplyParser.startElement(null, AcctMgrRPCReplyParser.MESSAGE_TAG, null, null);
        acctMgrRPCReplyParser.characters((MESSAGE + " 2").toCharArray(), 0, MESSAGE.length() + 2);
        acctMgrRPCReplyParser.endElement(null, AcctMgrRPCReplyParser.MESSAGE_TAG, null);
        acctMgrRPCReplyParser.endElement(null, AcctMgrRPCReplyParser.ACCT_MGR_RPC_REPLY_TAG, null);

        expected.error_num = 1;
        expected.messages.add(MESSAGE + " 1");
        expected.messages.add(MESSAGE + " 2");

        assertEquals(expected, acctMgrRPCReplyParser.getAccountMgrRPCReply());
    }
}