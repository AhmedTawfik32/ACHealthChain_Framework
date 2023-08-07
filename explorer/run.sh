
cd ../

sudo chmod +x ./test-network/

sudo chown -R ahmedtawfik ./test-network/

cd ./explorer/

sudo cp -R ../test-network/organizations/ .

sudo chown -R ahmedtawfik ./organizations/

#change name of secret key to priv_sk 

#sudo docker-compose up
