/*
"MinSrc.java - Minimal source program.
 *
 * Copyright (c) 2005-2019 Informatica Corporation. All Rights Reserved.
 * Permission is granted to licensees to use
 * or alter this software for any purpose, including commercial applications,
 * according to the terms laid out in the Software License Agreement.
 -
 - This source code example is provided by Informatica for educational
 - and evaluation purposes only.
 -
 - THE SOFTWARE IS PROVIDED "AS IS" AND INFORMATICA DISCLAIMS ALL WARRANTIES
 - EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION, ANY IMPLIED WARRANTIES OF
 - NON-INFRINGEMENT, MERCHANTABILITY OR FITNESS FOR A PARTICULAR
 - PURPOSE.  INFORMATICA DOES NOT WARRANT THAT USE OF THE SOFTWARE WILL BE
 - UNINTERRUPTED OR ERROR-FREE.  INFORMATICA SHALL NOT, UNDER ANY CIRCUMSTANCES, BE
 - LIABLE TO LICENSEE FOR LOST PROFITS, CONSEQUENTIAL, INCIDENTAL, SPECIAL OR
 - INDIRECT DAMAGES ARISING OUT OF OR RELATED TO THIS AGREEMENT OR THE
 - TRANSACTIONS CONTEMPLATED HEREUNDER, EVEN IF INFORMATICA HAS BEEN APPRISED OF
 - THE LIKELIHOOD OF SUCH DAMAGES.
 -
 - As you will see, there is no error handling in this example program.  A
 - production program would want to have better error handling, but for the
 - purposes of a minimal example, it would just be a distraction.  Also, a
 - production program would want to support a configuration file to override
 - default values on options.
 -
 - Build/run notes - Windows
 -   1. Make sure "java" and "javac" are both in your current path.
 -   2. Make sure C:\Program Files\29West\LBM_<VERS>\Win2k-i386\bin
 -      is also in your path.
 -   3. Compile using a command like:
 -          javac -classpath C:\path\to\lbmjarfile.jar MinSrc.java
 -   4. Run using a command like:
 -          java -classpath .;C:\path\to\lbmjarfile.jar MinSrc
 -
 - Build/run notes - UNIX
 -   1. Make sure "java" and "javac" are both in your current path.
 -   2. The appropriate library search path should be updated to include the
 -      LBM "bin" directory.  For example, on Linux:
 -        LD_LIBRARY_PATH="$HOME/lbm/LBM_<VERS>/<PLATFORM>/bin:$LD_LIBRARY_PATH"
 -        export LD_LIBRARY_PATH
 -      Alternatively, the shared library can be copied from the LBM "bin"
 -      directory to a directory which is already in the library search path.
 -   3. Compile using a command like:
 -          javac -classpath /path/to/lbmjarfile.jar MinSrc.java
 -   4. Run using a command like:
 -          java -classpath .:/path/to/lbmjarfile.jar MinSrc
 */

/*
 * Import everything from the LBM jar file.  In a real application, of course,
 * you should always import only those classes that you actually use.
 */
import com.latencybusters.lbm.*;

public class MinSrc {

    public static void main(String[] args) throws LBMException{

        /*
         * First, create a set of attributes for an LBM context.  Attributes
         * allow you to control programmatically most of the same LBM options
         * you can set in a configuration file.  In fact, there are a few
         * options that _must_ be set programmatically in this way. (Usually
         * options that set particular callbacks.)
         */
        LBMContextAttributes myAttributes = new LBMContextAttributes();

        /*
         * Now we'll create a context object.  A context is an environment in
         * which LBM functions.  Each context can have its own attributes and
         * configuration settings, which can be specified in an
         * LBMContextAttributes object like the one we just created.
         */
        LBMContext myContext = new LBMContext(myAttributes);

        /*
         * Lookup a topic object.  A topic object is little more than a string
         * (the topic name).  During operation, LBM keeps some state information
         * in the topic object as well.  The topic is bound to the containing
         * context, and will also be bound to a receiver object.  Topics can
         * also have a set of attributes; these are specified using either an
         * LBMReceiverAttributes object or an LBMSourceAttributes object.  The
         * simplest LBMTopic constructor makes a Receiver topic with the default
         * set of attributes.  However, there is a difference between "receiver"
         * topics and "source" topics.  To make a "source topic", we need to
         * use a constructor that lets us pass in an LBMSourceAttributes object.
         * Here, we pass in default attributes by creating a dummy
         * LBMSourceAttributes object.
         * Note that "Greeting" is the topic string. */
        LBMTopic myTopic = new LBMTopic(myContext, "Greeting", new LBMSourceAttributes());

        /*
         * Create the source object and bind it to a topic.  Sources must be
         * associated with a context.
         */
        LBMSource mySource = new LBMSource(myContext, myTopic);

        /*
         * Need to wait for receivers to find us before first send.  There are
         * other ways to accomplish this, but sleep is easy.  See https://communities.informatica.com/infakb/faq/5/Pages/80061.aspx
         * for details.
         */
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) { }

        /*
         * Send a message to the "Greeting" topic.  The flags make sure the
         * call to lbm_src_send doesn't return until the message is sent.
         */
        mySource.send("Hello!".getBytes(), "Hello!".length(), LBM.MSG_FLUSH | LBM.SRC_BLOCK);

        /*
         * We've sent our one message.  Let's close things down.
         */

        /*
         * For some transport types (mostly UDP-based), a short delay before
         * deleting the source is advisable.  Even though the message is sent,
         * there may have been packet loss, and some transports need a bit of
         * time to request re-transmission.  Also, if the above lbm_src_send call
         * didn't include the flush, some time might also be needed to empty the
         * batching buffer.
         */
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) { }

        /*
         * First, we close the source.
         */
        mySource.close();

        /*
         * Notice that we don't "close" the topic.  LBM keeps track of
         * topics for you.
         */


        /*
         * Now, we close the context itself.  Always close a context that is no
         * longer in use and won't be used again.
         */
        myContext.close();

    }  /* main */
}  /* class MinSrc */
