# a python script to split the dataset into 4 parts, each part is a csv file named as part1.csv, part2.csv, part3.csv, part4.csv respectively

import pandas as pd
import numpy as np

# read the dataset
df = pd.read_csv('ObesityDataSet_raw_and_data_sinthetic.csv')

# shuffle the dataset
df = df.sample(frac=1).reset_index(drop=True)

# split the dataset into 4 parts without keeping an empty row at the end
part1 = df.iloc[:len(df)//4]
part2 = df.iloc[len(df)//4:2*len(df)//4]
part3 = df.iloc[2*len(df)//4:3*len(df)//4]
part4 = df.iloc[3*len(df)//4:]

# make that the last row of each part is not empty
part1 = part1.dropna()
part2 = part2.dropna()
part3 = part3.dropna()
part4 = part4.dropna()

# check the length of each part
print(len(part1), len(part2), len(part3), len(part4))


# save the parts
part1.to_csv('part1.csv', index=False)
part2.to_csv('part2.csv', index=False)
part3.to_csv('part3.csv', index=False)
part4.to_csv('part4.csv', index=False)

print('Dataset split successfully!')