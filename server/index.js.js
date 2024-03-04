const express = require('express');
const admin = require('firebase-admin');
const stripe = require('stripe')('your_stripe_secret_key');

// Initialize Firebase Admin SDK
const serviceAccount = require('./path/to/serviceAccountKey.json');
admin.initializeApp({
  credential: admin.credential.cert(serviceAccount)
});

const app = express();
const port = 3000;

// Middleware to parse JSON bodies
app.use(express.json());

// POST endpoint for sending messages
app.post('/send-message', (req, res) => {
  const { senderId, receiverId, message } = req.body;

  // Construct the message payload
  const payload = {
    notification: {
      title: 'New Message',
      body: message
    }
  };

  // Get the FCM token of the receiver (assuming you have a way to store and retrieve tokens)
  admin.firestore().collection('users').doc(receiverId).get()
    .then(doc => {
      if (doc.exists) {
        const receiverToken = doc.data().fcmToken;

        // Send the message using FCM
        admin.messaging().sendToDevice(receiverToken, payload)
          .then(response => {
            console.log('Message sent successfully:', response);
            res.status(200).send('Message sent successfully');
          })
          .catch(error => {
            console.error('Error sending message:', error);
            res.status(500).send('Error sending message');
          });
      } else {
        console.error('Receiver not found');
        res.status(404).send('Receiver not found');
      }
    })
    .catch(error => {
      console.error('Error getting receiver:', error);
      res.status(500).send('Error getting receiver');
    });
});

app.post('/process-payment', async (req, res) => {
    const { token, amount, currency } = req.body;
  
    try {
      // Create a charge using Stripe
      const charge = await stripe.charges.create({
        amount: amount,
        currency: currency,
        description: 'Payment for service',
        source: token
      });
  
      // Handle successful charge
      console.log('Charge successful:', charge);
      res.status(200).send('Payment processed successfully');
    } catch (error) {
      // Handle charge error
      console.error('Error processing payment:', error);
      res.status(500).send('Error processing payment');
    }
  });

// Start the server
app.listen(port, () => {
  console.log(`Server running at http://localhost:${port}`);
});
